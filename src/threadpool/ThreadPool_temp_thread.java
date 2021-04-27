package threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool_temp_thread {

    final static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,4,10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(2), new MyDefaultThreadFactory(ThreadPool_temp_thread.class.getName()),new ThreadPoolExecutor.AbortPolicy());

    /**
     *  temp threads = maxNum - coreNum;
     *  pool size = coreNum + tempNum;
     *
     *  temp threads (线程临时工如果不起来且core线程一直不释放, 那么队列内将一直得不到执行)
     *  temp线程什么时候起来呢，在队列满时起来，且不是从队列里出队一个执行，如下(依据本例配置，开5个thread)
     *    core（2） always occupy         queue（2）      max-core = 2
     *  { core1(tsk1),core2(tsk2) } === |task3|task4|   { temp（task 5）}
     *
     *   当线程5执行完，temp queue 中 取出继续执行 task3 和 task4
     *   queue为空 且 keep alive time reach，temp thread 退出
     *
     *   question: 若core thread 和 temp thread 同时空闲而队列不为空，它们之间谁去执行呢？
     *
     */

    static Runnable runTaskLong = new Runnable() {
        @Override
        public void run() {
            System.out.println("long occupy..."+ Thread.currentThread().getName());
            for(;;){

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    static Runnable runTaskShort = new Runnable() {
        @Override
        public void run() {
            System.out.println("short occupy..."+ Thread.currentThread().getName());
            try {
                Thread.sleep(1000*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    public static void main(String[] args){

        poolExecutor.submit(runTaskLong);

        poolExecutor.submit(runTaskShort);

        poolExecutor.submit(runTaskShort);

        poolExecutor.submit(runTaskShort);

        poolExecutor.submit(runTaskShort);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String log = "pool_realsize: "+ poolExecutor.getPoolSize()+" ### queue_size: " + poolExecutor.getQueue().size();

                    System.out.println(log);
                }
            }
        }).start();

    }
}
