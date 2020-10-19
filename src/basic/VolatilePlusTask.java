package basic;

/**
 * description volatile不能代替锁，它只确保了变量的可见性
 *
 * @author 27771
 * create 2020-10-19 19:47
 **/
public class VolatilePlusTask {
    private static volatile int i = 0;

    private static class PlusTask implements Runnable{

        @Override
        public void run() {
            int rounds = 10000;
            for (int k = 0; k < rounds; k++) {i++;}
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for(int i = 0; i < threads.length; i++){
            Runnable target;
            threads[i] = new Thread(new PlusTask());
            threads[i].start();
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].join();
        }
        //因为volatile不能保证++操作原子性，所以i != 100000
        System.out.println(i);
    }
}