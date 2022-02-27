package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Student;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student student;
    private int startModelIndex;
    private int endModelIndex;
    private boolean sendTrainFlag;
    private boolean sendTestFlag;
    private Future future;
    private Object result;

    private static List<Student> studentsList;
    private static int startIndex;
    private static int endIndex;

    public StudentService(String name) {
        super(name);
        if(startIndex <= endIndex){
            student = studentsList.get(startIndex);
            startIndex ++;
        }
        startModelIndex = 0;
        endModelIndex = student.getModels().size() - 1;
        sendTrainFlag = false;
        sendTestFlag = false;
        future = null;
        result = null;
    }

    @Override
    protected void initialize() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        Map<Event<?>, Future> futureList = messageBus.getFuturesAssociatedWithEvents();

        subscribeBroadcast(PublishConferenceBroadcast.class, ev -> {
            AtomicInteger papersRead = new AtomicInteger();
            ev.getAggregatedResults().forEach((k, v) -> {
                if(k != this.student.getStudentNum()){
                    papersRead.addAndGet(v.size());
                }
            });
            this.student.setPapersRead(papersRead.intValue());
        });

        subscribeBroadcast(TickBroadcast.class, ev -> {
            if(ev.getTickTime() >= ev.getTotalTicks()){
                terminate();


            }

            if (startModelIndex <= endModelIndex){
                if(!sendTrainFlag){
                    future = sendEvent(student.getModels().get(startModelIndex).getTrainModelEvent());
                    sendTrainFlag = true;

                } else if (sendTrainFlag && !sendTestFlag){
                    if (future.isDone()) {
                        result = future.get();
                        future = sendEvent(student.getModels().get(startModelIndex).getTestModelEvent());
                        sendTestFlag = true;
                    }
                }else if (sendTrainFlag && sendTestFlag){
                    if (future.isDone()) {
                        result = future.get();
                        if (result.equals("Good")) {
                            future = sendEvent(student.getModels().get(startModelIndex).getPublishResultsEvent());
                            student.increasePublications();
                        }
                        sendTrainFlag = false;
                        sendTestFlag = false;
                        startModelIndex = startModelIndex + 1;
                    }
                }
            }
        });
    }

    public static void setStudentsList(List<Student> list){
        studentsList = list;
        startIndex = 0;
        endIndex = studentsList.size() - 1;
    }

}
