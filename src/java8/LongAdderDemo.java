package java8;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * description 更快的原子类：LongAdder
 *
 * @author 27771
 * create 2020-11-17 21:03
 **/
public class LongAdderDemo {

    private static final int MAX_THREADS = 100;
    private static final int TARGET_COUNT = 1000_0000;

    private static final AtomicLong ATOMIC_LONG = new AtomicLong(0);
    private static final LongAdder LONG_ADDER = new LongAdder();
    private static long count = 0;

    private static synchronized long increment() {
        return ++count;
    }

    private static synchronized long getCount() {
        return count;
    }

    public static void testSync() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[MAX_THREADS];
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i] = new Thread(() -> {
                long c = getCount();
                while (c < TARGET_COUNT) {
                    c = increment();
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println(count + " testSync : " + (end - start) + " ms");
    }

    public static void testAtomic() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[MAX_THREADS];
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i] = new Thread(() -> {
                long c = ATOMIC_LONG.get();
                while (c < TARGET_COUNT) {
                    c = ATOMIC_LONG.incrementAndGet();
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println(ATOMIC_LONG.get() + " testAtomic : " + (end - start) + " ms");
    }

    public static void testLongAdder() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[MAX_THREADS];
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i] = new Thread(() -> {
                long c = LONG_ADDER.sum();
                while (c < TARGET_COUNT) {
                    LONG_ADDER.increment();
                    c = LONG_ADDER.sum();
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < MAX_THREADS; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println(LONG_ADDER.sum() + " testLongAdder : " + (end - start) + " ms");
    }

    public static void main(String[] args) throws InterruptedException {
        // 按耗时升序排序 testAtomic testLongAdder testSync
        testSync();
        testAtomic();
        testLongAdder();
    }
}