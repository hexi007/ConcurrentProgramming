package concurrent_basic;

/**
 * description 等待和通知
 *
 * @author 27771
 * create 2020-10-19 18:40
 **/
public class SimpleWaitNotify {
    private static final Object object = new Object();

    private static class T1 extends Thread{
        @Override
        public void run() {
            //Object.wait()不是可以随便调用的，必须包含在对应的synchronized语句中，首先要获得目标对象的监视器
            synchronized (object){
                System.out.println(System.currentTimeMillis() + " : T1 start!");
                try {
                    System.out.println(System.currentTimeMillis() + " : T1 wait for object...");
                    //wait()执行之后会释放监视器，使其他等待object的线程不因为T1的休眠而无法正常执行
                    object.wait();
                    //被唤醒并不会立即执行后续代码，而是重新尝试获取监视器，如果无法获取还必须等待这个监视器
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + " : t1 end!");
            }
        }
    }

    private static class T2 extends Thread{
        @Override
        public void run() {
            synchronized (object){
                System.out.println(System.currentTimeMillis() + " : T2 start notify one Thread!");
                //调用notify()之前必须获得object的监视器
                object.notify();
                System.out.println(System.currentTimeMillis() + " : T2 end!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 有时候因为指令重排，T2线程先于T1执行，T1会永远等待下去
     * 1603105287554 : T2 start notify one Thread!
     * 1603105287562 : T2 end!
     * 1603105289564 : T1 start!
     * 1603105289564 T1 wait for object...
     */
    public static void main(String[] args) {
        Thread t1 = new T1();
        Thread t2 = new T2();

        t1.start();
        t2.start();
    }
}