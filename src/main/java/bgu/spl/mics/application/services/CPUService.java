package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CPU service is responsible for handling the "{@link CPU}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;
    private static List<CPU> cpusList;
    private static int startIndex;
    private static int endIndex;
    private Map<Class<? extends Message>, Callback<? extends Message>> callbacks = new HashMap<>();

    public CPUService(String name) {
        super(name);
        if(startIndex <= endIndex){
            cpu = cpusList.get(startIndex);
            startIndex ++;
        }
    }

    public static void initializeCpusList(List<CPU> list){
        cpusList = list;
        startIndex = 0;
        endIndex = cpusList.size() - 1;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, ev -> {
            if (cpu.shouldStartProcessing()) {
                cpu.startProcessing(ev.getTickTime());
            }
            if (cpu.shouldStopProcessing()) {
                cpu.stopProcessing(ev.getTickTime());
            }
            if(ev.getTickTime() >= ev.getTotalTicks()){
                terminate();
                Thread.currentThread().interrupt();

            }
        });
    }

    public static List<CPU> getCpusList(){
        return cpusList;
    }

}
