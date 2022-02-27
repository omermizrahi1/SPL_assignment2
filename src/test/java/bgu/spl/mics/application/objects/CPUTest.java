package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {

    private CPU cpu;
    Cluster cluster = Cluster.getInstance();

    @Before
    public void setUp() {
        cpu = new CPU(2);
        cluster.startProcessDataBatch(new DataBatch());
    }


    @Test
    public void shouldStartProcessingWhenStartProcessingTimeIs0() {
        boolean actualResult = cpu.shouldStartProcessing();
        assertTrue(actualResult);
    }

    @Test
    public void shouldNotBeAbleStartProcessingWhenEnquiredDataFromCluster() {
        cpu.startProcessing(1);
        boolean actualResult = cpu.shouldStartProcessing();
        assertFalse(actualResult);
    }
}