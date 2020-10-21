package concurrentpackage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description 锁申请等待限时
 *
 * @author 27771
 * create 2020-10-21 15:42
 **/
public class TimeLock implements Runnable{
    public static ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        try{
            //tryLock()第一个参数为等待时长，第二个为计时单位
            //超时为获得锁，返回false
            //tryLock()不带参数运行会立即尝试获取锁，线程不会等待，因此也不会死锁
            if(lock.tryLock(5, TimeUnit.MICROSECONDS)){
                //占用锁会保持锁6秒
                Thread.sleep(6000);
            } else {
                System.out.println(Thread.currentThread().getName() + " get lock failed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        TimeLock timeLock = new TimeLock();
        Thread t1 = new Thread(timeLock);
        t1.setName("Thread t1");
        Thread t2 = new Thread(timeLock);
        t2.setName("Thread t2");

        t1.start();
        t2.start();
    }
}