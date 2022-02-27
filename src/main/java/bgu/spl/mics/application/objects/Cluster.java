package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and met hods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

    private static Cluster instance = null;
    private List<DataBatch> dataBatchesToProcess = new ArrayList<>();
    private List<DataBatch> processedDataBatches = new ArrayList<>();

    // STATISTICS:
    private int numDataBatchesProcessed = 0;
    private int numOfCpuTimeUnits = 0;
    private int numOfGpuTimeUnits = 0;
    private List<String> namesModelsTrained = new ArrayList<>();

    private Object lock1 = new Object();
    private Object lock2 = new Object();


    public static Cluster getInstance() {
        if (instance == null) {
            instance = new Cluster();
        }
        return instance;
    }


    public void startProcessDataBatch(DataBatch dataBatch) {
        synchronized (dataBatchesToProcess) {
            dataBatchesToProcess.add(dataBatch);
        }
    }

    public DataBatch getDataBatchToProcess() {
        synchronized (dataBatchesToProcess) {
            if (!dataBatchesToProcess.isEmpty()) {
                DataBatch dataBatch = dataBatchesToProcess.get(0);
                dataBatchesToProcess.remove(0);
                return dataBatch;
            }
        }
        return null;
    }

    public boolean isDoneProcessedDataBatch(DataBatch dataBatch) {
        synchronized (processedDataBatches) {
            return processedDataBatches.contains(dataBatch);
        }
    }

    public void sendProcessedDataBatch(DataBatch dataBatch) {
        synchronized (processedDataBatches) {
            processedDataBatches.add(dataBatch);
        }
    }

    public void setNumDataBatchesProcessed() {
        synchronized (lock1) {
            this.numDataBatchesProcessed = this.numDataBatchesProcessed + 1;
        }
    }

    public void setNumOfCpuTimeUnits(int numOfCpuTimeUnits) {
        synchronized (lock1) {
            this.numOfCpuTimeUnits = this.numOfCpuTimeUnits + numOfCpuTimeUnits;
        }
    }

    public void setNumOfGpuTimeUnits(int numOfGpuTimeUnits) {
        synchronized (lock2) {
            this.numOfGpuTimeUnits = this.numOfGpuTimeUnits + numOfGpuTimeUnits;
        }
    }

    public void setNamesModelsTrained(String nameofModelTrained) {
        synchronized (namesModelsTrained) {
            this.namesModelsTrained.add(nameofModelTrained);
        }
    }

    public int getNumDataBatchesProcessed() {
        return numDataBatchesProcessed;
    }

    public int getNumOfCpuTimeUnits() {
        return numOfCpuTimeUnits;
    }

    public int getNumOfGpuTimeUnits() {
        return numOfGpuTimeUnits;
    }
}