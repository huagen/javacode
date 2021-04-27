package thread;

public class ThreeAlternateExecute {

    public static void main(String args[]){

        Object o = new Object();
        Runnable r1 = ()->{
            synchronized (o){

                o.notify();
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("r1 11111");
            }

        };
        Runnable r2 = ()->{
            System.out.println("r2 22222");
        };
        Runnable r3 = ()->{
            System.out.println("r3 33333");
        };

        Thread t1 = new Thread(r1);
        t1.setPriority(10);
        Thread t2 = new Thread(r2);
        t2.setPriority(5);
        Thread t3 = new Thread(r3);
        t3.setPriority(1);

        t1.start();
        t2.start();
        t3.start();
    }

}
