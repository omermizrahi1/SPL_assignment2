package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static ConcurrentMap<Class<? extends Message>, List<MicroService>> microServicesSubscribeToMessage = new ConcurrentHashMap<>();
	private static ConcurrentMap<MicroService, BlockingQueue> microServiceQueueMessage = new ConcurrentHashMap<>();
	private static ConcurrentMap<Event<?>, Future> futuresAssociatedWithEvents = new ConcurrentHashMap<>();
	private static ConcurrentMap<Class<? extends Message>, List<BlockingQueue>> roundRobin = new ConcurrentHashMap<>();
	private static MessageBusImpl instance = null;

	private Object lock1 = new Object();
	private Object lock2 = new Object();
	private Object lock3 = new Object();

	public static MessageBus getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}

	public MessageBusImpl(){}

	/**
	 * @pre int size = subscribedMicroService.size() <= 0
	 * @post size ++
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (lock1) {
			if (getSubscribedMicroServiceMessage(type) == null) {
				roundRobin.put(type, new ArrayList<>());
				microServicesSubscribeToMessage.put(type, new ArrayList<>());
			}
			microServicesSubscribeToMessage.get(type).add(m);
			roundRobin.get(type).add(microServiceQueueMessage.get(m));
			lock1.notifyAll();
		}
	}

	/**
	 * @pre int size = subscribedMicroService.size(); <=0
	 * @post size ++
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (lock2) {
			if (getSubscribedMicroServiceMessage(type) == null) {
				microServicesSubscribeToMessage.put(type, new ArrayList<>());
			}
			microServicesSubscribeToMessage.get(type).add(m);
			lock2.notifyAll();
		}
	}

	/**
	 * @pre microServiceQueueEvent.get(microService)!=null && int size = microServiceQueueEvent.get(microService).size(); <=0
	 * @post size ++
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (lock1) {
			while (getSubscribedMicroServiceMessage(e.getClass()) == null) {
				try {
					lock1.wait();
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		}

		synchronized (lock1) {
			if (getSubscribedMicroServiceMessage(e.getClass()) == null) {
				microServicesSubscribeToMessage.put(e.getClass(), new ArrayList<>());
			}
		}

		synchronized (lock3) {
			if (microServicesSubscribeToMessage.get(e.getClass()) != null) {
				if(roundRobin.get(e.getClass()).get(0) != null){
					Future<T> future = new Future<>();
					futuresAssociatedWithEvents.put(e, future);
					roundRobin.get(e.getClass()).get(0).add(e);
					BlockingQueue queue = roundRobin.get(e.getClass()).remove(0);
					roundRobin.get(e.getClass()).add(queue);
					lock3.notifyAll();
					return future;
				}
			}
		}
		return null;
	}

	/**
	 * @pre microServiceBroadcastEvent.get(microService)!=null && int size = microServiceBroadcastEvent.get(microService).size(); <=0
	 * @post size ++
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (lock2) {
			if (getSubscribedMicroServiceMessage(b.getClass()) == null) {
				try {
					lock2.wait();
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		}
		synchronized (lock3) {
			if (getSubscribedMicroServiceMessage(b.getClass()) != null) {
				int size = microServicesSubscribeToMessage.get(b.getClass()).size();
				for (int i = 0; i < size; i++) {
					MicroService microService = microServicesSubscribeToMessage.get(b.getClass()).get(i);
					BlockingQueue queue = microServiceQueueMessage.get(microService);
					if (queue != null) {
						queue.add(b);
						lock3.notifyAll();
					}
				}
			}
		}
	}

	/**
	 * @pre The future event is not resolved
	 * @post The future event is resolve && the result == result from the input
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (lock3) {
			Future<T> future = futuresAssociatedWithEvents.get(e);
			future.resolve(result);
		}
	}

	/**
	 * @pre
	 * @post
	 */
	@Override
	 public Message awaitMessage(MicroService m) throws InterruptedException {
		if (microServiceQueueMessage.get(m) == null) {
			throw new IllegalStateException("MicroService was never registered.");
		}
		synchronized (lock3){
			while (microServiceQueueMessage.get(m).isEmpty()) {
				lock3.wait();
			}
			return (Message) microServiceQueueMessage.get(m).remove();
		}
	}

	/**
	 * @pre microServiceQueue of Event == null
	 * @post microServiceQueue of Event != null && Empty
	 */
	@Override
	public void register(MicroService m) {
		synchronized (microServiceQueueMessage) {
			microServiceQueueMessage.put(m, new LinkedBlockingQueue());
		}
	}

	/**
	 * @pre microServiceQueueEvent != null && microServiceQueueBroadcast != null
	 * @post microServiceQueueEvent == null && microServiceQueueBroadcast == null
	 */
	@Override
	public void unregister(MicroService m) {
		synchronized (microServiceQueueMessage) {
			if (microServiceQueueMessage.get(m) == null) {
				return;
			}
			microServiceQueueMessage.remove(m);
		}
		synchronized (lock1) {
			synchronized (lock2) {
				microServicesSubscribeToMessage.forEach((k, v) -> {
					if (v.contains(m)) {
						v.remove(m);
					}
				});
			}
		}
		synchronized (lock3) {
			roundRobin.forEach((k, v) -> {
				if (v.contains(microServiceQueueMessage.get(m))) {
					v.remove(microServiceQueueMessage.get(m));
				}
			});
		}
	}

	/**
	 * @return The MicroService Subscribe to the Message
	 */
	public <T> List<MicroService> getSubscribedMicroServiceMessage(Class<? extends Message> type) {
		return microServicesSubscribeToMessage.get(type);
	}
	/**
	 * @return The MicroService Queue
	 */
	//TODO: if only for test remove
	public BlockingQueue getRegisteredMicroServiceQueue(MicroService service) {
		return microServiceQueueMessage.get(service);
	}
	/**
	 * @return T/F if Queue of Events & Broadcast created for MicroService
	 */
	public boolean isMicroServiceRegistered(MicroService service) {
		BlockingQueue serviceQueue = microServiceQueueMessage.get(service);
		return serviceQueue != null;

	}
	/**
	 * @return T/F if the Event is in the MicroService Queue
	 */
	public <T> boolean isInMicroServiceMessageQueue(Message e, MicroService m){
		return microServiceQueueMessage.get(m).contains(e);
	}

	public Map<Event<?>, Future> getFuturesAssociatedWithEvents(){
		return futuresAssociatedWithEvents;
	}

}
