package bgu.spl.mics.application.objects;

import bgu.spl.mics.Message;
import java.util.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private int numOfDataBatches;
    private List<DataBatch> batches;
    private int startIndex = 0;
    private int endIndex;
    private int vramSize;
    private Map<Class<? extends Message>,Queue<Message>> gpuMessage = new HashMap<>();
    private int numOfSentDataBatchesToCluster;
    private int totalProcessedDataBatches;
    private int numOfTrainedDataBatches;
    private boolean isAvailable = true;
    private int startTrainingTime;


    public GPU(String type) {
        this.type = determineGPUType(type);
        this.cluster = Cluster.getInstance();
        this.vramSize = getVramSize(this.type);
        this.model = null;
        this.batches = null;
        this.numOfDataBatches = 0;
        this.numOfTrainedDataBatches = 0;
        this.numOfSentDataBatchesToCluster = 0;
        this.totalProcessedDataBatches = 0;
        this.startTrainingTime = 0;
    }

    public GPU(Type type, Model model) {
        this.type = type;
        this.model = model;
        this.cluster = Cluster.getInstance();
        this.vramSize = getVramSize(type);
        this.batches = null;
        this.numOfDataBatches = 0;
        this.numOfTrainedDataBatches = 0;
        this.numOfSentDataBatchesToCluster = 0;
        this.totalProcessedDataBatches = 0;
        this.startTrainingTime = 0;
    }

    private int getVramSize(Type type) {
        switch (type) {
            case RTX3090:
                return 32;
            case RTX2080:
                return 16;
            case GTX1080:
                return 8;
        }
        return 0;
    }

    private int getTrainingTime(Type type) {
        switch (type) {
            case RTX3090:
                return 1;
            case RTX2080:
                return 2;
            case GTX1080:
                return 4;
        }
        return 0;
    }

    private Type determineGPUType(String type) {
        if (type.equals("RTX3090")) {
            return GPU.Type.RTX3090;
        } else if (type.equals("RTX2080")) {
            return GPU.Type.RTX2080;
        } else if (type.equals("GTX1080")) {
            return GPU.Type.GTX1080;
        }
        return null;
    }

    public Map<Class<? extends Message>, Queue<Message>> getGpuMessage() {
        return gpuMessage;
    }

    public Model getModel() {
        return model;
    }

    public int getVramSize() {return vramSize;}

    public boolean isAvailable() {return isAvailable;}

    public void setIsAvailable(){this.isAvailable = true;}

    public void setIsNotAvailable(){this.isAvailable = false;}

    public void setModel(Model model){this.model = model;}

    /**
     * @pre numOfSentDataBatchToCluster < numOfDataBatch
     * @post numOfSentDataBatchToCluster++
     */
    public Model sendDataBatchToCluster(Model model, int startIndex , int endIndex) {
        Cluster cluster = Cluster.getInstance();
        int min = Math.min(batches.size(), endIndex);
        this.endIndex = min;
        for (int i = startIndex; i < min; i++) {
            cluster.startProcessDataBatch(batches.get(i));
            numOfSentDataBatchesToCluster = numOfSentDataBatchesToCluster + 1;
        }
        return model;
    }
    /**
     * @return  l:  List<DataBatch>
     * @pre Data =! null
     * @post l.size() = Data.getSize()/1000
     */
    public List<DataBatch> divideDataToBatches(Model model) {
        int batchNum = model.getData().getSize() / 1000;
        Data data = model.getData();
        List<DataBatch> batchList = new ArrayList<>(batchNum);
        int startIndex = 0;
        for (int i = 0 ; i < batchNum; i++){
            batchList.add(new DataBatch(data , startIndex));
            startIndex = startIndex + 1000;
        }
        this.batches = batchList;
        return batchList;
    }

    public List<DataBatch> getBatchList(){
        return this.batches;
    }

    public int getBatchListSize(){
        return this.batches.size();
    }

    public DataBatch getDataBatch(int index) {
        return batches.get(index);
    }
    
    public int getStartIndex(){
        return startIndex;
    }

    public void setStartIndex(int startIndex) {this.startIndex = startIndex;}

    public int getEndIndex(){
        return endIndex;
    }

    public void setEndIndex(int endIndex) {this.endIndex = endIndex;}


    public void addTotalProcessedDataBatches(int numOfProcessedDataBatches){
        this.totalProcessedDataBatches = this.totalProcessedDataBatches + numOfProcessedDataBatches;
    }

    public void addTrainedDataBatches(int trainedDataBatches){
        this.numOfTrainedDataBatches = this.numOfTrainedDataBatches + trainedDataBatches;
    }


    /**
     * @pre if (numOfTrainedDataBatch==0) this.getModel().getStatus = PreTrained, if (numOfTrainedDataBatch>0) this.getModel().getStatus == Training
     * @post numOfTrainedDataBatch ++
     * @inv numOfTrainedDataBatch =< numOfDataBatch
     */
    public void startTrainDataBatch(DataBatch dataBatch) {
        dataBatch.doneTrained();
    }


    public int getAllowedNumberOfProcessedBatches() {
        return vramSize - numOfSentDataBatchesToCluster;
    }

    public int getNumOfProcessedDataBatch() {return totalProcessedDataBatches;}

    public boolean canSendDataBatchToCluster() {
        return numOfSentDataBatchesToCluster < vramSize;
    }

    public int getNumOfTrainedDataBatch() {
        return numOfTrainedDataBatches;
    }

    public boolean shouldStartTraining() {return startTrainingTime == 0;}

    public boolean shouldStopTraining() {
        return startTrainingTime != 0;
    }

    /**
     * @pre startTrainingTime == 0
     * @post Update the startTrainingTime -> currentTick
     */
    public void startTraining(int currentTick) {startTrainingTime = currentTick;}

    /**
     * @pre startTrainingTime != 0 && currentTick - startTrainingTime >= getRequiredTimeToTrainDataBatch()
     * @post DataBatch is Trained
     */
    public boolean stopTraining(int currentTick, DataBatch dataBatch , Model model) {
        if (currentTick - startTrainingTime >= getTrainingTime(this.type)) {
            dataBatch.doneTrained();
            numOfSentDataBatchesToCluster = numOfSentDataBatchesToCluster - 1;
            startTrainingTime = 0;
            cluster.setNamesModelsTrained(model.getName());
            cluster.setNumOfGpuTimeUnits(getTrainingTime(this.type));
            numOfTrainedDataBatches = numOfTrainedDataBatches + 1;
            addTotalProcessedDataBatches(1);
            return true;
        }
        return false;
    }

    private class Fraction {
        private int numerator;
        private int denominator;
        private double doubleValue;

        public Fraction(int n, int m) {
            numerator = n;
            denominator = m;
            doubleValue = (double) n / (double) m;
        }

        public int getNumerator() {
            return numerator;
        }

        public int getDenominator() {
            return denominator;
        }

        public double getDoubleValue() {
            return doubleValue;
        }
    }

    private Fraction determineSuccessProbability(Student.Degree degree) {
        switch (degree) {
            case PhD:
                return new Fraction(3, 5);
            case MSc:
                return new Fraction(4, 5);
        }
        return new Fraction(1, 1);
    }

    private Model.Results determineResultByProbability(Fraction prob) {
        Random rnd = new Random();
        int random = rnd.nextInt(prob.getDenominator());
        if (random <= prob.getNumerator())
            return Model.Results.Good;
        else
            return Model.Results.Bad;
    }

    public void testModel(Model model, Student.Degree degree) {
        Fraction prob = determineSuccessProbability(degree);
        Model.Results result = determineResultByProbability(prob);
        model.setResults(result);
    }


}
