package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;

	public TimeService(int speed, int duration, List<Thread> threadList) {
		super("Time Service");
		this.speed = speed;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, ev -> {
			if (ev.getTickTime() >= ev.getTotalTicks()) {
				terminate();
			}
		});

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			private int tickTime = 0;
			@Override
			public void run() {
				if (tickTime < duration) {
					tickTime = tickTime + 1;
					sendBroadcast(new TickBroadcast(tickTime, duration));
				} else {
					timer.cancel();
                    terminate();
				}
			}
		}, 500 ,speed);
	}
}