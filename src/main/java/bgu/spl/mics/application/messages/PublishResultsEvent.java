package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model> {

    private String senderName;
    private Model model;
    private int studentNum;

    public PublishResultsEvent(String senderName, Model model, int studentNum) {
        this.senderName = senderName;
        this.model = model;
        this.studentNum = studentNum;
    }

    public String getSenderName() {
        return senderName;
    }

    public Model getModel() {
        return model;
    }

    public int getStudentNum() {
        return studentNum;
    }

    @Override
    public String toString() {
        return "PublishResultsEvent";
    }
}
