package concurrentpackage;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description 重入锁的好搭档：Condition条件
 * condition必须和重入锁配合使用，就像wait(),notify()和synchronized必须配合在一起一样
 *
 * @author 27771
 * create 2020-10-21 16:36
 **/
public class ReenterLockCondition implements Runnable {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    @Override
    public void run() {
        try{
            lock.lock();
            //当线程使用condition.await()时，要求线程必须持有相关的重入锁
            //在condition.await()调用后，这个线程会释放这把锁
            condition.await();
            System.out.println("Thread is going on");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            lock.unlock();
            }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockCondition r = new ReenterLockCondition();
        Thread t = new Thread(r);
        t.start();
        Thread.sleep(2000);
        lock.lock();
        //再次强调condition.signal()必须和lock配合使用
        //condition.signal()从当前condition对象的等待队列中唤醒一个线程
        condition.signal();
        //signal()之后一般需要释放相关锁，谦让给被唤醒的线程
        lock.unlock();
    }
}