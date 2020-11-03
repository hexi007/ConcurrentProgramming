package concurrent_basic;

/**
 * description 挂起和继续执行
 *
 * @author 27771
 * create 2020-10-19 19:06
 **/
public class BadSuspend {
    private static Object object = new Object();

    private static class ChangeObjectThread extends Thread{
        public ChangeObjectThread(String name){
            super.setName(name);
        }

        @Override
        public void run() {
            synchronized (object){
                System.out.println("in " + getName());
                //suspend不会释放任何锁资源，如果resume操作先于suspend前执行，那么被挂起线程很难有机会执行
                //被挂起线程状态还是Runnable，这会影响对系统当前状态的判断
                Thread.currentThread().suspend();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ChangeObjectThread t1 = new ChangeObjectThread("t1");
        ChangeObjectThread t2 = new ChangeObjectThread("t2");
        t1.start();
        Thread.sleep(100);
        t2.start();
        //因为resume调用顺序的问题，resume没有生效，并导致t2永远挂起并占用了t1的锁
        t1.resume();
        t2.resume();
        t1.join();
        t2.join();
    }
}