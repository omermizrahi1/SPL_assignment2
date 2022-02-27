package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Root{
    @JsonProperty("Students")
    public List<Student> students;
    @JsonProperty("GPUS")
    public List<String> gpus;
    @JsonProperty("CPUS")
    public List<Integer> cpus;
    @JsonProperty("Conferences")
    public List<ConfrenceInformation> conferences;
    @JsonProperty("TickTime")
    public int tickTime;
    @JsonProperty("Duration")
    public int duration;
    public List<GPU> gpusList;
    public List<CPU> cpusList;


    public Root()
    {
        super();
    }

    public void projectInitializer(){
        initializeGpusList();
        initializeCpusList();
        initializeConfrenceInformation();
        initializeStudents();
    }

    private void initializeStudents() {
        int i = 0;
        for (Student student : students){
            for (Model model : student.getModels()){
                model.setResults(Model.Results.None);
                model.setStatus(Model.Status.PreTrained);
                Data.Type dataType = getDataType(model);
                model.setData(new Data(dataType, model.getSize()));
                model.setTrainModelEvent(new TrainModelEvent(student.getName(), model));
                model.setTestModelEvent(new TestModelEvent(student.getName(), model ,student.getStatus().toString()));
                model.setPublishResultsEvent(new PublishResultsEvent(student.getName(), model , i));
            }
            student.setDegree();
            student.setStudentNum(i);
            i = i + 1;
        }
        StudentService.setStudentsList(students);
    }

    private Data.Type getDataType(Model model) {
        if(toUpperWithoutSpaces(model.getType()).equals("IMAGES")){
            return Data.Type.Images;
        }else if(toUpperWithoutSpaces(model.getType()).equals("TEXT")){
            return Data.Type.Text;
        }else if(toUpperWithoutSpaces(model.getType()).equals("TABULAR")){
            return Data.Type.Tabular;
        }
        return null;
    }

    private void initializeConfrenceInformation() {
        ConferenceService.setConferenceInformationList(conferences);
    }

    private void initializeCpusList() {
        cpusList = new ArrayList<>();
        for (Integer cpuCoresNum : cpus) {
            cpusList.add(new CPU(cpuCoresNum));
        }
        CPUService.initializeCpusList(cpusList);
    }

    private void initializeGpusList() {
        gpusList = new ArrayList<>();
        for (String gpuType : gpus) {
            gpusList.add(new GPU(toUpperWithoutSpaces(gpuType)));
        }
        GPUService.initializeGpusList(gpusList);
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

    public List<Student> getStudentList(){
        return students;
    }



    public String toString() {
        return "Root {" + '\n' +
                "  students = " + '\n' + "  " + students + '\n' +
                "  GPUS = " + gpus + '\n' +
                "  CPUS = " + cpus + '\n' +
                "  conferences = " + conferences + '\n' +
                "  tickTime = " + tickTime + '\n' +
                "  duration = " + duration + '\n' +
                "  }";
    }
}
