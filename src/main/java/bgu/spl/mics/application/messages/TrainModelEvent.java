package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model> {
    private String senderName;
    private Model model;

    public TrainModelEvent(String senderName){this.senderName = senderName;} //for test

    public TrainModelEvent(String senderName, Model model) {
        this.senderName = senderName;
        this.model = model;
    }

    public String getSenderName() {
        return senderName;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "TrainModelEvent";
    }
}
