package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class GPUTest {

    private GPU gpu;
    private GPU gpu2;
    private Student student;
    private Data data;
    private Data data2;
    private Model model;
    private Model model2;

    @Before
    public void setUp() {
        student = new Student("name", "department", Student.Degree.MSc, 0, 0);
        data = new Data(Data.Type.Images, 1000);
        model = new Model("model", data, Model.Status.PreTrained, Model.Results.None);
        gpu = new GPU(GPU.Type.RTX3090, model);

        data2 = new Data(Data.Type.Images, 34000);
        model2 = new Model("model", data2, Model.Status.PreTrained, Model.Results.None);
        gpu2 = new GPU(GPU.Type.RTX3090, model);
        gpu2.divideDataToBatches(model2);
    }

    @Test
    public void shouldGetOneBatchWhenDataSizeIs1000() {
        List<DataBatch> actualResult = gpu.divideDataToBatches(model);
        assertEquals(1, actualResult.size());
    }

    @Test
    public void shouldReturn32BatchesForGPURTX3090(){
        int actualResult = gpu.getAllowedNumberOfProcessedBatches();
        assertEquals(32, actualResult);
    }

    @Test
    public void shouldReturnFalseWhenAllowedNumberOfProcessedBatchesOccupiedForGPURTX3090() {
        for (int i = 0 ; i < 32 ; i++) {
            gpu2.sendDataBatchToCluster(model2,0, gpu2.getVramSize());
        }
        boolean actualResult = gpu2.canSendDataBatchToCluster();
        assertEquals(false, actualResult);
    }

    @Test
    public void shouldIncreaseNumOfProcessedDataBatchWhenExecuteStartProcessDataBatch() {
        int numOfProcessedDataBatchBeforeExecute = 0;
        gpu2.addTotalProcessedDataBatches(1);
        int numOfProcessedDataBatchAfterExecute = gpu2.getNumOfProcessedDataBatch();
        assertEquals(numOfProcessedDataBatchBeforeExecute + 1 , numOfProcessedDataBatchAfterExecute);
    }

    @Test
    public void shouldIncreaseNumOfTrainedDataBatchWhenExecuteStartTrainDataBatch() {
        int numOfTrainedDataBatchBeforeExecute = gpu2.getNumOfTrainedDataBatch();
        gpu2.startTrainDataBatch(gpu2.getDataBatch(0));
        gpu2.addTrainedDataBatches(1);
        int numOfTrainedDataBatchAfterExecute = gpu2.getNumOfTrainedDataBatch();
        assertEquals(numOfTrainedDataBatchBeforeExecute + 1 , numOfTrainedDataBatchAfterExecute);
    }
}