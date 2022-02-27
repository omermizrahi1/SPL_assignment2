package bgu.spl.mics.application.objects;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Cluster cluster;
    private int startProcessingTime;
    private DataBatch dataBatch;

    public CPU(int cores) {
        this.cores = cores;
        this.dataBatch = null;
        this.cluster = Cluster.getInstance();
        this.startProcessingTime = 0;
    }

    @Override
    public String toString() {
        return "CPU {" + '\n' +
                "  cores = " + cores + '\n' +
                "  startProcessingTime = " + startProcessingTime + '\n' +
                "  }"+ '\n';
    }

    private int getRequiredTimeToProcessDataBatch() {
        Data.Type type = dataBatch.getType();
        int ticks = 0;
        switch(type){
            case Images:
                ticks = 4;
                break;
            case Text:
                ticks = 2;
                break;
            case Tabular:
                ticks = 1;
                break;
        }
        return (32/cores)*ticks;
    }

    public boolean shouldStartProcessing() {
        return startProcessingTime == 0;
    }

    public boolean shouldStopProcessing() {
        return startProcessingTime != 0;
    }


    /**
     * @pre startProcessingTime == 0
     * @post Update the startProcessingTime -> currentTick
     */
    public void startProcessing(int currentTick) {
        dataBatch = cluster.getDataBatchToProcess();
        if (dataBatch != null) {
            startProcessingTime = currentTick;
        }
    }

    /**
     * @pre startProcessingTime != 0 && currentTick - startProcessingTime >= getRequiredTimeToProcessDataBatch()
     * @post Processed DataBatch sent to the cluster
     */
    public void stopProcessing(int currentTick) {
        if (currentTick - startProcessingTime >= getRequiredTimeToProcessDataBatch()) {
            dataBatch.doneProcess();
            cluster.sendProcessedDataBatch(dataBatch);
            startProcessingTime = 0;
            cluster.setNumDataBatchesProcessed();
            cluster.setNumOfCpuTimeUnits(getRequiredTimeToProcessDataBatch());
        }
    }

}
