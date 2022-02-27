package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Leaf {

    public List<StudentOut> students;
    public List<Conference> conferences;
    public int cpuTimeUsed;
    public int gpuTimeUsed;
    public int batchesProcessed;

    public class TrainedModel{
        public String name;
        public Data data;
        public String status;
        public String results;

        public TrainedModel(Model model){
            this.name = model.getName();
            this.data = model.getData();
            this.status = model.getStatus().toString();
            this.results = model.getResults().toString();
        }
    }

    public class StudentOut{
        public String name;
        public String department;
        public String status;
        public int publications;
        public int papersRead;
        public List<Leaf.TrainedModel> trainedModels;

        public StudentOut(Student student){
            this.name = student.getName();
            this.department = student.getDepartment();
            this.status = student.getStatus().toString();
            this.publications = student.getPublications();
            this.papersRead = student.getPapersRead();
            trainedModels = initializeTrainedModel(student.getModels());
        }
    }

    public class Publication{
        public String name;
        public Data data;
        public String status;
        public String results;

        public Publication(Model model) {
            this.name = model.getName();
            this.data = model.getData();
            this.status = model.getStatus().toString();
            this.results = model.getResults().toString();
        }
    }

    public class Conference{
        public String name;
        public int date;
        public List<Leaf.Publication> publications;

        public Conference(ConferenceService conferenceService) {
            this.name = conferenceService.getConfrenceInformation().getName();
            this.date = conferenceService.getConfrenceInformation().getDate();
            this.publications = initializePublications(conferenceService);
        }
    }

    public Leaf (List<Student> students, List<ConferenceService> conferences, int timeUsedCPU, int timeUsedGPU, int numBatches){
        initializeStudents(students);
        initializeConference(conferences);
        setCpuTimeUsed(timeUsedCPU);
        setGpuTimeUsed(timeUsedGPU);
        setBatchesProcessed(numBatches);
    }

    private void initializeStudents(List<Student> students){
        List<Leaf.StudentOut> studentsList = new ArrayList<>();
        for (Student student : students){
            studentsList.add(new Leaf.StudentOut(student));
        }
        this.students = studentsList;
    }

    private List<Leaf.TrainedModel> initializeTrainedModel(List<Model> studentModels){
        List<Leaf.TrainedModel> trainedModelsList = new ArrayList<>();
        for (Model model : studentModels){
            trainedModelsList.add(new Leaf.TrainedModel(model));
        }
        return trainedModelsList;
    }

    private void initializeConference(List<ConferenceService> conferences){
        List<Conference> conferencesList = new ArrayList<>();
        for (ConferenceService conferenceService : conferences){
            conferencesList.add(new Conference(conferenceService));
        }
        this.conferences = conferencesList;
    }

    private List<Leaf.Publication> initializePublications(ConferenceService conferenceService){
        List<Leaf.Publication> publications = new ArrayList<>();
        conferenceService.getAggregatedResults().forEach((k, v) -> {
            for (Model model : v){
                publications.add(new Publication(model));
            }
        });
        return publications;
    }

    private void setCpuTimeUsed(int timeUsed){
        this.cpuTimeUsed = timeUsed;
    }

    private void setGpuTimeUsed(int timeUsed){
        this.gpuTimeUsed = timeUsed;
    }

    private void setBatchesProcessed(int numBatches){
        this.batchesProcessed = numBatches;
    }

}