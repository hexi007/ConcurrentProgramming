package lock_optimization;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * description 无锁的线程安全整数 ： AtomicInteger
 * CSA 指令实现 AtomicInteger 的原子性操作
 * 使用 AtomicInteger 会比锁具有更好的性能
 *
 * @author 27771
 * create 2020-10-31 19:33
 **/
public class AtomicIntegerDemo {
    private static final AtomicInteger I = new AtomicInteger();

    private static class AddThread implements Runnable {

        @Override
        public void run() {
            int addTimes = 10000;
            for(int k = 0; k < addTimes; k++) {
                I.incrementAndGet();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int threadSize = 10;
        Thread[] threads = new Thread[threadSize];
        for(int k = 0; k < threadSize; k++) {
            threads[k] = new Thread(new AddThread());
            threads[k].start();
        }
        for(int k = 0; k < threadSize; k++) {
            threads[k].join();
        }
        System.out.println(I);
    }
}