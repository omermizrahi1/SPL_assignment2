package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation confrenceInformation;
    private static List<ConfrenceInformation> confrenceInformationList;
    private static int startIndex;
    private static int endIndex;
    private Map<Integer, List<Model>> aggregatedResults = new HashMap<>();

    public ConferenceService(String name) {
        super(name);
        if(startIndex <= endIndex){
            confrenceInformation = confrenceInformationList.get(startIndex);
            startIndex ++;
        }
    }

    @Override
    protected void initialize() {
        MessageBus messageBus = MessageBusImpl.getInstance();

        subscribeEvent(PublishResultsEvent.class, ev -> {
            receiveResult(ev.getStudentNum(),ev.getModel());
        });

        subscribeBroadcast(TickBroadcast.class, ev -> {
            if(ev.getTickTime() >= confrenceInformation.getDate() || ev.getTickTime() >= ev.getTotalTicks()){
                messageBus.sendBroadcast(new PublishConferenceBroadcast(getAggregatedResults()));
                terminate();
                Thread.currentThread().interrupt();
            }
        });

    }

    public static void setConferenceInformationList(List<ConfrenceInformation> list){
        confrenceInformationList = list;
        startIndex = 0;
        endIndex = confrenceInformationList.size() - 1;
    }

    public static List<ConfrenceInformation> getConferenceInformationList(){
        return confrenceInformationList;
    }

    public void receiveResult(Integer studentNum, Model model){
        if(!aggregatedResults.containsKey(studentNum)){
            aggregatedResults.put(studentNum,new ArrayList<>());
            aggregatedResults.get(studentNum).add(model);
        }else {
            aggregatedResults.get(studentNum).add(model);
        }
    }

    public Map<Integer, List<Model>> getAggregatedResults() {
        return aggregatedResults;
    }

    public ConfrenceInformation getConfrenceInformation() {
        return confrenceInformation;
    }
}

