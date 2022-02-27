package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    //Represent a Deep Learning model.
    public enum Status {PreTrained, Training, Trained, Tested}
    public enum Results {None, Good, Bad} //can be “None” (for a model not in status tested)

    private String name; //name of the model.
    private Data data; //the data the model should train on.
    private Status status;
    private Results results;
    private int studentNum;

    private TrainModelEvent trainModelEvent;
    private TestModelEvent testModelEvent;
    private PublishResultsEvent publishResultsEvent;

    private String type; //set to the Date
    private int size; //set to the Date

    public Model(){
        super();
    }

    public String toString() {
        return "Model{\n" +
                "  name = " + name + '\n' +
                "  data = " + data +'\n' +
                "  status = " + status +'\n' +
                "  results = " + results +'\n' +
                "  type = " + type + '\n' +
                "  size = " + size +'\n' +
                "  }" +'\n';
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setStudentNum(int num){this.studentNum = num; }


    public Model(String name,Data data, Status status, Results results) {
        this.name = name;
        this.status = status;
        this.results = results;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Data getData() {return data;}

    public Status getStatus() {
        return status;
    }

    public Results getResults() {
        return results;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(int size) {
        this.size = size;
    }



    public TrainModelEvent getTrainModelEvent() {
        return trainModelEvent;
    }

    public TestModelEvent getTestModelEvent() {
        return testModelEvent;
    }

    public PublishResultsEvent getPublishResultsEvent() {
        return publishResultsEvent;
    }

    public void setTrainModelEvent(TrainModelEvent trainModelEvent) {
        this.trainModelEvent = trainModelEvent;
    }

    public void setTestModelEvent(TestModelEvent testModelEvent) {
        this.testModelEvent = testModelEvent;
    }

    public void setPublishResultsEvent(PublishResultsEvent publishResultsEvent) {
        this.publishResultsEvent = publishResultsEvent;
    }
}
