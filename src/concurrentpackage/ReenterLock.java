package concurrentpackage;

import java.util.concurrent.locks.ReentrantLock;

/**
 * description 重入锁
 *
 * @author 27771
 * create 2020-10-21 15:08
 **/
public class ReenterLock implements Runnable {

    /**
     * 重入锁允许一个线程两次获得一把锁，一个线程申请多少次锁，在释放锁时就必须释放相同次数
     */
    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    @Override
    public void run() {
        int rounds = 10000000;
        for(int j = 0; j < rounds; j++){
            lock.lock();
            try {
                i++;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new ReenterLock());
        Thread t2 = new Thread(new ReenterLock());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(i);
    }
}