package patterns_and_algorithms;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description 并行奇偶交换排序
 * create 2020-11-05 20:59
 *
 * @author 27771
 **/
public class MultiOddEvenSort {
    private static int[] arr;
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public MultiOddEvenSort() {
        int len = 20;
        arr = new int[len];
        for(int i = 0; i < len; i++){
            arr[i] = (int)(Math.random() * len);
        }
    }

    public void oddEvenSort(){
        int exchangeFlag = 1, start = 0, step = 2;
        while (exchangeFlag == 1 || start == 1) {
            exchangeFlag = 0;
            for(int i = start; i < arr.length - 1; i += step){
                if(arr[i] > arr[i + 1]){
                    swap(i, i + 1);
                    exchangeFlag = 1;
                }
            }
            start = 1 - start;
        }
    }

    private static final AtomicBoolean EXCHANGE_FLAG = new AtomicBoolean(true);

    public static class OddEvenSortTask implements Runnable {
        int i;
        CountDownLatch latch;

        public OddEvenSortTask(int i, CountDownLatch latch) {
            this.i = i;
            this.latch = latch;
        }

        @Override
        public void run() {
            if(arr[i] > arr[i + 1]){
                MultiOddEvenSort.swap(i,i + 1);
                if(!EXCHANGE_FLAG.get()){
                    EXCHANGE_FLAG.compareAndSet(false, true);
                }
            }
            latch.countDown();
        }
    }

    public void pOddEvenSort() throws InterruptedException {
        int start = 0;
        while (EXCHANGE_FLAG.get() || start == 1){
            if(EXCHANGE_FLAG.get()){
                EXCHANGE_FLAG.compareAndSet(true, false);
            }
            int step = 2;
            CountDownLatch latch = new CountDownLatch(arr.length / step -
                    (arr.length % 2 == 0 ? start : 0));
            for(int i = start; i < arr.length - 1; i += step){
                EXECUTOR.submit(new OddEvenSortTask(i, latch));
            }
            latch.await();
            start = 1 - start;
        }
    }

    private static void swap(int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) throws InterruptedException {
        MultiOddEvenSort m = new MultiOddEvenSort();
        m.oddEvenSort();
        m.pOddEvenSort();
        System.out.println(Arrays.toString(arr));
    }
}