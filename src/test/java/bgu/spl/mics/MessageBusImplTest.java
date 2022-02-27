package bgu.spl.mics;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MessageBusImplTest {

    private MessageBus messageBus;
    private GPUService gpuServiceFirst;
    private GPUService gpuServiceSecond;
    private GPUService gpuServiceThird;
    private GPUService gpuServiceFourth;
    private TrainModelEvent trainModelEvent;
    private ExampleEvent exampleEvent;
    private TestModelEvent testModelEvent;

    @Before
    public void setUp() throws Exception {
        messageBus = MessageBusImpl.getInstance();
        gpuServiceFirst = new GPUService();
        gpuServiceSecond = new GPUService();
        gpuServiceThird = new GPUService();
        gpuServiceFourth = new GPUService();
        trainModelEvent = new TrainModelEvent("");
        exampleEvent = new ExampleEvent("");
        testModelEvent = new TestModelEvent("");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMessageBusImpl() throws Exception {
        ShouldCreateQueueForAppropriateServiceWhenServiceRegistered();
        ShouldAddMicroServiceToEventListWhenExecuteSubscribe();
        ShouldIncreaseAppropriateServiceQueueWhenEventSent();
        ShouldAddMicroServiceToBroadcastListWhenExecuteSubscribe();
        ShouldIncreaseAppropriateServiceQueueWhenBroadcastSent();
        ShouldDeleteAppropriateServiceQueueWhenServiceUnregister();
    }


    public void ShouldCreateQueueForAppropriateServiceWhenServiceRegistered() {
        messageBus.register(gpuServiceFirst);
        assertTrue(messageBus.isMicroServiceRegistered(gpuServiceFirst));
        assertFalse(messageBus.isMicroServiceRegistered(gpuServiceSecond));
    }

    public void ShouldAddMicroServiceToEventListWhenExecuteSubscribe() {
        messageBus.subscribeEvent(testModelEvent.getClass(), gpuServiceFirst);
        List<MicroService> subscribedMicroService = messageBus.getSubscribedMicroServiceMessage(testModelEvent.getClass());
        int expectedNumOfSubscribedServices = 1;
        assertEquals(expectedNumOfSubscribedServices, subscribedMicroService.size());
        assertTrue(subscribedMicroService.contains(gpuServiceFirst));
        assertFalse(subscribedMicroService.contains(gpuServiceSecond));
    }

    public void ShouldIncreaseAppropriateServiceQueueWhenEventSent() {
        messageBus.sendEvent(testModelEvent);
        BlockingQueue registeredMicroServiceQueue = messageBus.getRegisteredMicroServiceQueue(gpuServiceFirst);
        int expectedMessagesInQueue = 1;
        assertEquals(expectedMessagesInQueue, registeredMicroServiceQueue.size());
    }

    public void ShouldAddMicroServiceToBroadcastListWhenExecuteSubscribe() {
        messageBus.subscribeBroadcast(PublishConferenceBroadcast.class, gpuServiceFirst);
        List<MicroService> subscribedMicroService = messageBus.getSubscribedMicroServiceMessage(PublishConferenceBroadcast.class);
        int expectedNumOfSubscribedServices = 1;
        assertEquals(expectedNumOfSubscribedServices, subscribedMicroService.size());
        assertTrue(subscribedMicroService.contains(gpuServiceFirst));
        assertFalse(subscribedMicroService.contains(gpuServiceSecond));
    }


    public void ShouldIncreaseAppropriateServiceQueueWhenBroadcastSent() {
        messageBus.register(gpuServiceSecond);
        messageBus.subscribeBroadcast(PublishConferenceBroadcast.class, gpuServiceSecond);
        messageBus.sendBroadcast(new PublishConferenceBroadcast(""));
        BlockingQueue registeredMicroServiceQueue = messageBus.getRegisteredMicroServiceQueue(gpuServiceSecond);
        int expectedMessagesInQueue = 1;
        assertEquals(expectedMessagesInQueue, registeredMicroServiceQueue.size());
    }


    public void ShouldDeleteAppropriateServiceQueueWhenServiceUnregister() {
        assertTrue(messageBus.isMicroServiceRegistered(gpuServiceFirst));
        messageBus.unregister(gpuServiceFirst);
        assertFalse(messageBus.isMicroServiceRegistered(gpuServiceFirst));
    }

    @Test
    public void ShouldCompleteTheEventThatSent() throws InterruptedException {
        messageBus.register(gpuServiceThird);
        messageBus.subscribeEvent(exampleEvent.getClass(),gpuServiceThird);
        Future <String> future = messageBus.sendEvent(exampleEvent);
        assertFalse("Future not resolved", future.isDone());
        messageBus.complete(exampleEvent, "done");
        assertTrue("Future resolved", future.isDone());
        assertTrue(future.get()=="done");

    }

    @Test
    public void ShouldTakeMessageFromQueueWhenMServiceAndMessageExists() throws InterruptedException {
        messageBus.register(gpuServiceFourth);
        messageBus.subscribeEvent(trainModelEvent.getClass(),gpuServiceFourth);
        messageBus.sendEvent(trainModelEvent);
        assertTrue(messageBus.isInMicroServiceMessageQueue(trainModelEvent,gpuServiceFourth));
        messageBus.awaitMessage(gpuServiceFourth);
        assertFalse(messageBus.isInMicroServiceMessageQueue(trainModelEvent,gpuServiceFourth));
    }

}