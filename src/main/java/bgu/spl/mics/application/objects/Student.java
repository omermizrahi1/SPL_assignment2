package bgu.spl.mics.application.objects;

import java.util.*;


/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        PhD,MSc
    }

    private String name;
    private String department;
    private Degree status;
    private int publications = 0;
    private int papersRead = 0;
    private List <Model> models;
    private int studentNum;

    public Student(){
        super();
    }

    public Student(String name, String department, Degree status, int publications, int papersRead) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = publications;
        this.papersRead = papersRead;
    }

    public String toString() {
        return "Student{" + '\n' +
                "  name = " + name + '\n' +
                "  department = " + department + '\n' +
                "  status = " + status + '\n' +
                "  publications = " + publications + '\n' +
                "  papersRead = " + papersRead + '\n' +
                "  models = " + '\n' + "  " + models + '\n' +
                "  }" + '\n';
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public List<Model> getModels() {
        return models;
    }

    public void increasePublications() {
        this.publications = this.publications + 1;
    }

    public void setPublications(int publications) {
        this.publications = this.publications + publications;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead =  this.papersRead + papersRead;
    }

    public void setStudentNum(int num){this.studentNum = num; }



    public void setDegree(){
        String temp = this.status.toString();
        Degree tempDegree;
        if(toUpperWithoutSpaces(temp).equals("PHD")){
            tempDegree = Degree.PhD;
        }else if(toUpperWithoutSpaces(temp).equals("MSC")){
            tempDegree = Degree.MSc;
        }else {
            tempDegree = null;
        }
        this.status = tempDegree;
    }

    public String toUpperWithoutSpaces(String string){
        String output = "";
        for (int i = 0; i < string.length(); i++){
            if(string.charAt(i) != ' '){
                output = output + string.charAt(i);
            }
        }
        return output.toUpperCase();
    }

}