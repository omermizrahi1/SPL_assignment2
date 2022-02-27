package bgu.spl.mics.application.services;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import java.util.*;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent}
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private static List<GPU> gpusList;
    private static int startIndex;
    private static int endIndex;

    public GPUService(String name) {
        super(name);
        if(startIndex <= endIndex){
            gpu = gpusList.get(startIndex);
            startIndex ++;
        }
    }

    public static void initializeGpusList(List<GPU> list){
        gpusList = list;
        startIndex = 0;
        endIndex = gpusList.size() - 1;
    }

    public GPUService(){
        super("GPU NO. "+ new Random().nextInt(1000));
    }

    @Override
    protected void initialize() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        Cluster cluster = Cluster.getInstance();

        subscribeBroadcast(TickBroadcast.class, ev ->{
            if(ev.getTickTime() >= ev.getTotalTicks()){
                terminate();
                Thread.currentThread().interrupt();

            }

            if (!gpu.isAvailable()){
                List<DataBatch> dataBatches = gpu.getBatchList();
                if(gpu.shouldStartTraining()){
                    if(gpu.getStartIndex() < dataBatches.size()){
                        gpu.getModel().setStatus(Model.Status.Training);
                        if(cluster.isDoneProcessedDataBatch(dataBatches.get(gpu.getStartIndex()))){
                            gpu.startTraining(ev.getTickTime());
                        }
                    }
                }
                if(gpu.shouldStopTraining()){
                    if(gpu.stopTraining(ev.getTickTime(),dataBatches.get(gpu.getStartIndex()),gpu.getModel())){
                        gpu.setStartIndex(gpu.getStartIndex() + 1);
                    }
                }
                if(gpu.canSendDataBatchToCluster()){
                    int numSendDataBatch = gpu.getAllowedNumberOfProcessedBatches();
                    gpu.setEndIndex(gpu.getEndIndex() + numSendDataBatch);
                    gpu.sendDataBatchToCluster(gpu.getModel(), gpu.getStartIndex(), gpu.getEndIndex());
                }

                if(gpu.getNumOfTrainedDataBatch() == gpu.getBatchListSize()){
                    gpu.setIsAvailable();
                    gpu.getModel().setStatus(Model.Status.Trained);
                    complete(gpu.getModel().getTrainModelEvent(), gpu.getModel());
                }

            } else {
                if(!gpu.getGpuMessage().isEmpty()) {
                    if (gpu.getGpuMessage().containsKey(TestModelEvent.class) &&
                            !gpu.getGpuMessage().get(TestModelEvent.class).isEmpty()) {
                        TestModelEvent message = (TestModelEvent) gpu.getGpuMessage().get(TestModelEvent.class).poll();
                        gpu.testModel(message.getModel(),message.getDegree());
                        messageBus.getFuturesAssociatedWithEvents().get(ev).resolve(message.getModel().getResults().toString());

                    } else if (gpu.getGpuMessage().containsKey(TrainModelEvent.class) &&
                            !gpu.getGpuMessage().get(TrainModelEvent.class).isEmpty()) {
                        TrainModelEvent message = (TrainModelEvent) gpu.getGpuMessage().get(TrainModelEvent.class).poll();
                        this.TrainModelEvent(message);
                    }
                }
            }
        });

        subscribeEvent(TrainModelEvent.class, ev ->{
            this.TrainModelEvent(ev);
        });

        subscribeEvent(TestModelEvent.class, ev ->{
            gpu.testModel(ev.getModel(),ev.getDegree());
            messageBus.getFuturesAssociatedWithEvents().get(ev).resolve(ev.getModel().getResults().toString());
        });
    }

    public static List<GPU> getGpusList(){
        return gpusList;
    }

    public void TrainModelEvent(TrainModelEvent ev){
        if(gpu.isAvailable()){
            gpu.setIsNotAvailable();
            gpu.setModel(ev.getModel());
            List<DataBatch> dataBatches = gpu.divideDataToBatches(ev.getModel());
            Model model = gpu.sendDataBatchToCluster(ev.getModel(), 0, gpu.getVramSize());
        } else {
            if(gpu.getGpuMessage() != null){
                if (!gpu.getGpuMessage().containsKey(TrainModelEvent.class)){
                    gpu.getGpuMessage().put(TrainModelEvent.class, new LinkedList<>());
                    gpu.getGpuMessage().get(TrainModelEvent.class).add(ev);
                } else {
                    if (gpu.getGpuMessage().get(TrainModelEvent.class) != null){
                        gpu.getGpuMessage().get(TrainModelEvent.class).add(ev);
                    }
                }
            }
        }
    }
}
