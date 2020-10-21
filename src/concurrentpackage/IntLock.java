package concurrentpackage;

import java.util.concurrent.locks.ReentrantLock;

/**
 * description 重入锁的中断响应,等待过程中线程可以被中断并放弃所有资源
 *
 * @author 27771
 * create 2020-10-21 15:24
 **/
public class IntLock implements Runnable {
    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();
    boolean lock;

    public IntLock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        try{
            if(lock){
                //先占用lock1，在占用lock2，形成死锁
                //lockInterruptibly()表示这是一个可以对中断响应的锁申请动作
                //等待锁的过程中，可以响应中断
                lock1.lockInterruptibly();
                try{
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock2.lockInterruptibly();
            } else {
                lock2.lockInterruptibly();
                try{
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock1.lockInterruptibly();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //lock.isHeldByCurrentThread()的作用是查询当前线程是否保持此锁定
            if(lock1.isHeldByCurrentThread()){
                lock1.unlock();
            }
            if(lock2.isHeldByCurrentThread()){
                lock2.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " : 线程退出");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        IntLock intLock1 = new IntLock(true);
        Thread t1 = new Thread(intLock1);
        IntLock intLock2 = new IntLock(false);
        Thread t2 = new Thread(intLock2);

        t1.start();
        t2.start();

        //主线程休眠，两个线程死锁
        Thread.sleep(1000);

        //中断t2线程，t2放弃对lock1的申请，同时释放已获得lock2
        //t1线程因此可以顺利得到lock2而继续执行下去
        t2.interrupt();
    }
}