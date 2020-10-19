package basic;

/**
 * description 等待线程结束和谦让
 * Thread.yield()会让当前进程让出cpu，之后还会进行CPU争夺，但不一定能再次被分配到
 * @author 27771
 * create 2020-10-19 19:32
 **/
public class JoinMain {
    private static int i = 0;

    private static class AddThread extends Thread{
        @Override
        public void run() {
            int rounds = 10000000;
            while(i < rounds) {i++;}
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AddThread addThread = new AddThread();
        addThread.start();
        //join表示主线程愿意等到addThread执行完毕，join返回时addThread已经执行完成
        addThread.join();
        System.out.println(i);
    }
}