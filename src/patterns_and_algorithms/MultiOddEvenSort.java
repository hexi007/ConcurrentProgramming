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

    /**
     * 随机化 arr 数组
     */
    public MultiOddEvenSort() {
        int len = 20;
        arr = new int[len];
        for(int i = 0; i < len; i++){
            arr[i] = (int)(Math.random() * len);
        }
    }

    /**
     *  冒泡排序
     */
    public void bubbleSort() {
        for(int i = arr.length - 1; i >= 0; i--){
            for(int j = 0; j < i; j++){
                if(arr[j] > arr[j + 1]){
                    swap(i, j);
                }
            }
        }
    }

    /**
     * 奇偶交换排序
     */
    public void oddEvenSort(){
        /**
         *  exchangeFlag 数据交换标志位
         *  start        起始位置
         *  step         步幅
         */
        int exchangeFlag = 1, start = 0, step = 2;
        //只要发生了数据交换或者起始位置是1，就在执行一次排序
        while (exchangeFlag == 1 || start == 1) {
            //重置数据交换标志位为 0
            exchangeFlag = 0;
            //间隔为 2 的冒泡排序
            for(int i = start; i < arr.length - 1; i += step){
                if(arr[i] > arr[i + 1]){
                    swap(i, i + 1);
                    exchangeFlag = 1;
                }
            }
            //奇偶交换起始位置
            start = 1 - start;
        }
    }

    /**
     *  EXCHANGE_FLAG 数据交换标志原子操作位
     */
    private static final AtomicBoolean EXCHANGE_FLAG = new AtomicBoolean(true);

    public static class OddEvenSortTask implements Runnable {
        int i;
        CountDownLatch latch;

        public OddEvenSortTask(int i, CountDownLatch latch) {
            this.i = i;
            this.latch = latch;
        }

        /**
         *  数据交换操作
         */
        @Override
        public void run() {
            if(arr[i] > arr[i + 1]){
                MultiOddEvenSort.swap(i,i + 1);
                //只有当前标志为 false 时将其置为 true
                if(!EXCHANGE_FLAG.get()){
                    EXCHANGE_FLAG.compareAndSet(false, true);
                }
            }
            latch.countDown();
        }
    }

    /**
     *  并行奇偶交换操作
     */
    public void pOddEvenSort() throws InterruptedException {
        int start = 0;
        // 只要数据交换标志为 true 或者起始位置是1，就执行一次排序
        while (EXCHANGE_FLAG.get() || start == 1){
            // 重置数据交换标志位为 false
            if(EXCHANGE_FLAG.get()){
                EXCHANGE_FLAG.compareAndSet(true, false);
            }
            int step = 2;
            // 线程数 arr.length / step - (arr.length % 2 == 0 ? start : 0)
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