package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    //Represents a Batch of data
    private Data data; //The Data the batch belongs to
    private int startIndex; //The index of the first sample in the batch.
    private boolean isProcessed;
    private boolean isTrained;

    private Data.Type type;
    private GPU gpu = null;

    public DataBatch() {

    }

    public DataBatch(Data data, int startIndex) {
        this.type = data.getType();
        this.data = data;
        this.startIndex = startIndex;
        isProcessed = false;
        isTrained = false;
    }

    public boolean isProcessed() {return isProcessed;}

    public void doneProcess() {isProcessed = true;}

    public boolean isTrained() {return isTrained;}

    public void doneTrained() {isTrained = true;}

    public Data.Type getType() {
        return type;
    }

    public GPU getGpu() {
        return gpu;
    }

    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }
}
