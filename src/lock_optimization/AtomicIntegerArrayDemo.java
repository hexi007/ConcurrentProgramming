package lock_optimization;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * description 数组也能无锁 ： AtomicIntegerArray
 *
 * @author 27771
 * create 2020-10-31 20:56
 **/
public class AtomicIntegerArrayDemo {
    private static final AtomicIntegerArray ARRAY = new AtomicIntegerArray(10);

    public static class AddThread implements Runnable {
        @Override
        public void run() {
            int kTimes = 10000;
            for(int i = 0; i < kTimes; i++){
                //getAndIncrement(i) 将第 i 个下标的元素加一
                ARRAY.getAndIncrement(i % ARRAY.length());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int threadSize = 10;
        Thread[] threads = new Thread[threadSize];
        for(int i = 0; i < threadSize; i++){
            threads[i] = new Thread(new AddThread());
            threads[i].start();
        }
        for(int i = 0; i < threadSize; i++){
            threads[i].join();
        }
        System.out.println(ARRAY);
    }
}