package concurrentpackage;

import java.util.concurrent.locks.ReentrantLock;

/**
 * description 公平锁，公平锁的实现必然需要一个有序队列，所以成本高，性能相对低
 * 公平的锁可以保证先后顺序，不会产生饥饿现象
 *
 * @author 27771
 * create 2020-10-21 16:13
 **/
public class FairLock implements Runnable{
    /**
     * fair默认为false，为true表示锁是公平的
     */
    public static ReentrantLock fairLock = new ReentrantLock(true);

    @Override
    public void run() {
        while(true){
            try {
                fairLock.lock();
                System.out.println(Thread.currentThread().getName() + " get lock");
            } finally {
                fairLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        //设置非公平锁后一个线程会倾向于再次获取已经持有的锁，分配高效但无公平可言
        FairLock fairLock = new FairLock();
        Thread t1 = new Thread(fairLock, "Thread t1");
        Thread t2 = new Thread(fairLock, "Thread t2");

        t1.start();
        t2.start();
    }
}