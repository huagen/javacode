package thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class TwoThreadPlusPlus {
    /**
     * 两个线程将 int 变量 由 0 ->20000 0000
     *
     */
    static long beginTime = 0L;

    public static void main(String[] args){

        //  method();
         method2();
    }

    static volatile int i = 0;
    static void method(){

        CountDownLatch ctl = new CountDownLatch(2);
        Object o = new Object();
        Runnable r1 = ()->{
            int j = 10000_0000;
            while (j-->0) {
             synchronized (o){
                 i++;
             }
            } // end for
            ctl.countDown();
        };

        Runnable r2 = ()->{
            int j = 10000_0000;
            while (j-->0) {
                synchronized (o){
                    i++;
                }
            } // end for
            ctl.countDown();
        };

        Thread t1 = new Thread(r1,"thread 1");
        Thread t2 =new Thread(r2, "thread 2");
        t1.start();
        t2.start();

        beginTime = System.nanoTime();
        try {
            ctl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Value::"+i+" method consume time :: "+(System.nanoTime() - beginTime)+" in nanoTime");

    }

    //static volatile int i2 = 0;
    static AtomicInteger intRise = new AtomicInteger(0);
    static void method2(){

        CountDownLatch ctl = new CountDownLatch(2);
        Object o = new Object();
        Runnable r1 = ()->{
            int j = 10000_0000;
            while (j-->0) {

                   // i2++;
                intRise.incrementAndGet();
            } // end for
            ctl.countDown();
        };

        Runnable r2 = ()->{
            int j = 10000_0000;
            while (j-->0) {

                    //i2++;
                intRise.incrementAndGet();
            } // end for
            ctl.countDown();
        };

        Thread t1 = new Thread(r1,"thread 1");
        Thread t2 =new Thread(r2, "thread 2");
        t1.start();
        t2.start();

        beginTime = System.nanoTime();
        try {
            ctl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Value::"+ intRise.intValue()+" method consume time :: "+(System.nanoTime() - beginTime)+" in nanoTime");

    }
}
