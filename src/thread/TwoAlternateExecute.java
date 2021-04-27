package thread;

import java.util.concurrent.LinkedTransferQueue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.locks.LockSupport;

public class TwoAlternateExecute {

    static long beginTime = 0L;
    static final  char[] charArray = new char[]{'a','b','c','d','e','f','g','h'};
    static final  char[]  intArray = new char[]{'1','2','3','4','5','6','7','8'};

    public static void main(String[] args){

       // method1(); // synchronized wait notify
       // method2(); // LockSupport.park() unpark 輕量級
        method3(); // transferQueue
    }

    static void method3(){

        TransferQueue transferQueue = new LinkedTransferQueue(); // 注意transfer这里只是  LinkedTransferQueue的一个特别用法
        Runnable r1 = ()->{
            beginTime = System.nanoTime();

            for (char c : charArray) {

                try {
                    transferQueue.transfer(c);  // 两个方法都会阻塞，注意可加门栓保证时序
                    System.out.println(transferQueue.take());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } // end for
        };

        Runnable r2 = ()->{
            for (char i : intArray) {
                try {
                    System.out.println(transferQueue.take());
                    transferQueue.transfer(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } // end for
            System.out.println("method 2 consume time :: "+(System.nanoTime() - beginTime)+" in nanoTime");
        };

        t1 = new Thread(r1,"print char");
        t2 =new Thread(r2, "print int");

        t1.start();
        t2.start();
    }

    static Thread t1 = null, t2 = null;
    static void method2(){

        Runnable r1 = new Runnable() {
            @Override
            public void run() {

                beginTime = System.nanoTime();

                for (char c : charArray) {

                    System.out.println(c);
                    LockSupport.unpark(t2);
                    LockSupport.park();  // 阻塞 ，所以不能放在unpark之前，类于wait notify

                } // end for

            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {

                for (char i : intArray) {
                    LockSupport.park(); // 如果此句在r1的unpark后执行会不会有问题
                    System.out.println(i);
                    LockSupport.unpark(t1);

                } // end for
                System.out.println("method 2 consume time :: "+(System.nanoTime() - beginTime)+" in nanoTime");
            }
        };

         t1 = new Thread(r1,"print char");
         t2 =new Thread(r2, "print int");

         t1.start();
         t2.start();
    }

    static void method1(){

        CountDownLatch cdl = new CountDownLatch(1); // CountDownLatch 的作用 也可以采用标志自旋方式
        Object obj = new Object();

        Runnable r1 = new Runnable() {
            @Override
            public void run() {

                beginTime = System.nanoTime();
                synchronized (obj) {
                    for (char c : charArray) {
                        // 考虑当锁加在这里应当注意些什么
                        System.out.println(c);
                        if(cdl.getCount()>0)
                            cdl.countDown();
                        obj.notify();
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } // end for
                    obj.notify();
                }
            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    for (char i : intArray) {
                        // 考虑当锁加在这里应当注意些什么
                        System.out.println(i);
                        obj.notify();
                        try {
                            obj.wait(); // 一定是先notify，再wait阻塞
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } // end for
                    obj.notify();
                    System.out.println("method 1 consume time :: "+(System.nanoTime() - beginTime)+" in nanoTime");
                }
            }
        };

        new Thread(r1,"print char").start();

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(r2, "print int").start();
    }
}
