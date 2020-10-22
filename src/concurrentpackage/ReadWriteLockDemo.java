package concurrentpackage;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * description 读写锁
 * 读——读不互斥，读——写互斥，写——写互斥
 *
 * @author 27771
 * create 2020-10-22 19:34
 **/
public class ReadWriteLockDemo {
    private static Lock lock = new ReentrantLock();
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static Lock readLock = readWriteLock.readLock();
    private static Lock writeLock = readWriteLock.writeLock();
    private int value;

    public Integer readHandle (Lock lock) throws InterruptedException {
        try {
            lock.lock();
            //书上说读操作耗时越多，读写锁的优势就越明显，但实验结果二者差别可以忽视
            Thread.sleep(500);
        } finally {
            lock.unlock();
        }
        return value;
    }

    public void writeLock (Lock lock, int input) throws InterruptedException {
        try {
            lock.lock();
            Thread.sleep(100);
            value = input;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final ReadWriteLockDemo demo = new ReadWriteLockDemo();

        Runnable readRunnable = () -> {
            try {
                //选择哪种锁来进行读操作
                //demo.readHandle(readLock);
                demo.readHandle(lock);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable writeRunnable = () -> {
            try {
                //选择哪种锁来进行写操作
                //demo.writeLock(writeLock, new Random().nextInt());
                demo.writeLock(lock, new Random().nextInt());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        //计算程序运行时间
        long startTime = System.currentTimeMillis();

        int readThreadSize = 18, writeThreadSize = 2;
        Thread[] readThread = new Thread[readThreadSize];
        for (int i = 0; i < readThreadSize; i++){
            readThread[i] = new Thread(readRunnable);
            readThread[i].start();
            readThread[i].join();
        }
        Thread[] writeThread = new Thread[writeThreadSize];
        for (int i = 0; i < writeThreadSize; i++){
            writeThread[i] = new Thread(writeRunnable);
            writeThread[i].start();
            writeThread[i].join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间： " + (endTime - startTime) + " ms");
    }
}