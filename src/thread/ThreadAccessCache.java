package thread;

public class ThreadAccessCache {

    //static volatile boolean flag = true;
    static boolean flag = true;
    public static void main(String[] args){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){

                }
                System.out.println("While thread exit.");
            }
        },"While thread").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag = false;
                System.out.println("Stop thread exit.");
            }
        },"While thread").start();
    }
}
