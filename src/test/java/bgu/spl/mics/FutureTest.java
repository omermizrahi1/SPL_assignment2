package bgu.spl.mics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;

public class FutureTest {

    static Future future;

    @Before
    public void setUp() throws Exception {
        future = new Future();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() throws InterruptedException {
        Object o =  new Object();
        Thread t = new Thread(() -> {
           try{
               sleep(10000);
           }
           catch (Exception e){
               System.out.println(e.getMessage());
           }
           future.resolve(o);
        });
        t.start();
        long startTime = System.currentTimeMillis();
        Object shouldBeO = future.get();
        long elapsedTime = System.currentTimeMillis() - startTime; //elapsed time since get() was used
        assertEquals(o,shouldBeO);
        assertTrue(elapsedTime > 100);
    }

    @Test
    public void getByTime (){
        Object o =  new Object();
        Thread t = new Thread(() -> {
            try{
                sleep(10000);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
            future.resolve(o);
        });
        t.start();
        long startTime = System.currentTimeMillis();
        Object shouldBeO = future.get(5000, MILLISECONDS);
        assertEquals(shouldBeO,null);
        shouldBeO = future.get(System.currentTimeMillis() - startTime, MILLISECONDS);
        Assert.assertEquals(o,shouldBeO);
    }

    @Test
    public void ShouldDoneObjectWhenObjectResolved() {
        future.resolve(new Object());
        boolean actualResult = future.isDone();
        assertTrue(actualResult);
    }

    @Test
    public void ShouldReturnTrueWhenObjectBeenResolved() {
        future.resolve(new Object());
        boolean actualResult = future.isDone();
        assertTrue(actualResult);
    }

    @Test
    public void ShouldReturnTrueWhenObjectNotBeenResolved() {
        boolean actualResult = future.isDone();
        assertFalse(actualResult);
    }
}