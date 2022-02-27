package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishConferenceBroadcast implements Broadcast {

    private String result; // for test
    private Map<Integer, List<Model>> aggregatedResults = new HashMap<>();

    public PublishConferenceBroadcast(Map<Integer, List<Model>> aggregatedResults) {
        this.aggregatedResults = aggregatedResults;
    }

    public PublishConferenceBroadcast(String result) {
        this.result = result;
    }

    public String getResult() { //for test
        return result;
    }

    public Map<Integer, List<Model>> getAggregatedResults() {
        return aggregatedResults;
    }

    @Override
    public String toString() {
        return "PublishConferenceBroadcast";
    }
}
