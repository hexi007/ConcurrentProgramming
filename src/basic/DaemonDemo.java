package basic;

/**
 * description 守护线程,如果用户线程全部结束，则守护线程也会结束
 *
 * @author 27771
 * create 2020-10-20 16:19
 **/
public class DaemonDemo {
    private static class DaemonThread extends Thread{
        @Override
        public void run() {
            while (true) {
                System.out.println("I am alive!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new DaemonThread();
        //如果thread.setDaemon(true)和thread.start()顺序调换会出异常
        //因此即使main线程退出，thread也会继续执行
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(2000);
    }
}