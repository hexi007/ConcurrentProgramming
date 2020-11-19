package java8;

import java.util.Random;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * description LongAdder 的功能增强版：LongAccumulator
 *
 * @author 27771
 * create 2020-11-19 19:31
 **/
public class LongAccumulatorDemo {

    /**
     * accumulator 过滤最大值， 传入 Long::max 句柄
     */
    private final LongAccumulator accumulator = new LongAccumulator(Long::max,
            Long.MIN_VALUE);
    private final Thread[] threads = new Thread[1000];

    public void test() {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                long value = new Random().nextLong();
                accumulator.accumulate(value);
            });
            threads[i].start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(accumulator.longValue());
    }

    public static void main(String[] args) {
        LongAccumulatorDemo demo = new LongAccumulatorDemo();
        demo.test();
    }
}