package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Leaf;
import bgu.spl.mics.application.objects.Root;
import bgu.spl.mics.application.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPathIn = args[0];
        try {
            Root root = objectMapper.readValue(new File(jsonPathIn), Root.class);
            root.projectInitializer();

            List<StudentService> studentServiceList = new ArrayList<>();
            int studentCounter = root.getStudentList().size();
            for (int i = 0; i < studentCounter; i++) {
                studentServiceList.add(new StudentService("Student " + i));
            }

            List<GPUService> gpuServiceList = new ArrayList<>();
            int gpuCounter = GPUService.getGpusList().size();
            for (int i = 0; i < gpuCounter; i++) {
                gpuServiceList.add(new GPUService("GPU " + i));
            }

            List<CPUService> cpuServiceList = new ArrayList<>();
            int cpuCounter = CPUService.getCpusList().size();
            for (int i = 0; i < cpuCounter; i++) {
                cpuServiceList.add(new CPUService("CPU " + i));
            }

            List<ConferenceService> conferenceServiceList = new ArrayList<>();
            int conferenceInformationCounter = ConferenceService.getConferenceInformationList().size();
            for (int i = 0; i < conferenceInformationCounter; i++) {
                conferenceServiceList.add(new ConferenceService("ConferenceInformation " + i));
            }

            List<Thread> threadListList = new ArrayList<>();
            for (int i = 0; i < studentServiceList.size(); i++) {
                threadListList.add(new Thread(studentServiceList.get(i)));
            }
            for (int i = 0; i < gpuServiceList.size(); i++) {
                threadListList.add(new Thread(gpuServiceList.get(i)));
            }
            for (int i = 0; i < cpuServiceList.size(); i++) {
                threadListList.add(new Thread(cpuServiceList.get(i)));
            }
            for (int i = 0; i < conferenceServiceList.size(); i++) {
                threadListList.add(new Thread(conferenceServiceList.get(i)));
            }

            int tick = root.tickTime;
            int duration = root.duration;
            TimeService timeService = new TimeService(tick, duration,threadListList);
            threadListList.add(new Thread(timeService));

            for (int i = 0; i < threadListList.size(); i++) {
                threadListList.get(i).start();
            }
            for (int i = 0; i < threadListList.size(); i++) {
                threadListList.get(i).join();
            }

            Cluster cluster = Cluster.getInstance();
            Leaf leaf = new Leaf(root.students, conferenceServiceList,cluster.getNumOfCpuTimeUnits() , cluster.getNumOfGpuTimeUnits(), cluster.getNumDataBatchesProcessed());
            String jsonPathOut = args[1];
            File file = new File(jsonPathOut);
            PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            ObjectMapper objectMapperOut = new ObjectMapper();
            fileWriter.write(objectMapperOut.writerWithDefaultPrettyPrinter().writeValueAsString(leaf));
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}