package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model> {

    public enum Degree {
        PhD,MSc
    }

    private String senderName;
    private Model model;
    private Student.Degree status;


    public TestModelEvent(String senderName) { // For Test
        this.senderName = senderName;
    }

    public TestModelEvent(String senderName, Model model, String status) {
        this.senderName = senderName;
        this.model = model;

        Student.Degree tempDegree;
        if(toUpperWithoutSpaces(status).equals("PHD")){
            tempDegree = Student.Degree.PhD;
        }else if(toUpperWithoutSpaces(status).equals("MSC")){
            tempDegree = Student.Degree.MSc;
        }else {
            tempDegree = null;
        }
        this.status = tempDegree;
    }

    public String getSenderName() {
        return senderName;
    }

    public Student.Degree getDegree() {return status;}

    public Model getModel(){return model;}

    public String toUpperWithoutSpaces(String string){
        String output = "";
        for (int i = 0; i < string.length(); i++){
            if(string.charAt(i) != ' '){
                output = output + string.charAt(i);
            }
        }
        return output.toUpperCase();
    }

    @Override
    public String toString() {
        return "TestModelEvent";
    }
}
