package patterns_and_algorithms;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description 改进的插入排序：希尔排序
 * create 2020-11-06 14:56
 *
 * @author 27771
 **/
public class ShellSortTask {
    private static int[] arr;
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /**
     * 随机化 arr 数组
     */
    public ShellSortTask() {
        int len = 20;
        arr = new int[len];
        for(int i = 0; i < len; i++){
            arr[i] = (int)(Math.random() * len);
        }
    }

    /**
     *  插入排序
     */
    public void insertSort(){
        int len = arr.length;
        for(int i = 1; i < len; i++){
            //key 是要准备插入的元素
            int key = arr[i], j = i - 1;
            while (j >= 0 && arr[j] > key){
                arr[j + 1] = arr[j];
                j--;
            }
            //找到合适的位置插入 key
            arr[j + 1] = key;
        }
    }

    /**
     * 希尔排序：将数组根据间隔划分为若干个子数组，子数组相互穿插 <br/>
     * 每次排序时，分别对每一个子数组进行排序
     */
    public void shellSort(){
        //h 为，每次排序间隔
        int h = 1, step = 3;
        //计算初始排序同时也是最大的间隔
        while (h <= arr.length / step) {
            h = h * step + 1;
        }
        while (h > 0){
            for(int i = h; i < arr.length; i++){
                if(arr[i] < arr[i - h]){
                    int key = arr[i], j = i - h;
                    while(j >= 0 && arr[j] > key){
                        arr[j + h] = arr[j];
                        j -= h;
                    }
                    arr[j + h] = key;
                }
            }
            //一趟排序完成后递减间隔 h，进行更加精细的排序
            //直到 h = 1 时，等于一次完全插入排序
            h = (h - 1) / step;
        }
    }

    public static class ShellSortTak implements Runnable {

        int i, h;
        CountDownLatch latch;

        public ShellSortTak(int i, int h, CountDownLatch latch) {
            this.i = i;
            this.h = h;
            this.latch = latch;
        }

        /**
         *  每次只计算当前下标 i 开始的间隔为 h 的一趟插入排序
         */
        @Override
        public void run() {
            if(arr[i] < arr[i - h]){
                int key = arr[i], j = i - h;
                while(j >= 0 && arr[j] > key){
                    arr[j + h] = arr[j];
                    j -= h;
                }
                arr[j + h] = key;
            }
            latch.countDown();
        }
    }

    /**
     *  并行希尔排序
     */
    public void pShellSort() throws InterruptedException {
        int h = 1, step = 3;
        while (h <= arr.length / step) {
            h = h * step + 1;
        }
        CountDownLatch latch = null;
        while (h > 0) {
            //h >= 4 时才进行多线程排序
            //同时工作的线程总数为 arr.length - h
            if(h >= 4){
                latch = new CountDownLatch(arr.length - h);
            }
            for(int i = h; i < arr.length; i++){
                if(h >= 4) {
                    //提交 arr.length - h 个线程工作
                    EXECUTOR.execute(new ShellSortTak(i, h, latch));
                } else {
                    //h < 4 时退化为普通希尔排序
                    if(arr[i] < arr[i - h]){
                        int key = arr[i], j = i - h;
                        while(j >= 0 && arr[j] > key){
                            arr[j + h] = arr[j];
                            j -= h;
                        }
                        arr[j + h] = key;
                    }
                }
            }
            assert latch != null;
            latch.await();
            h = (h - 1) / step;
        }
        EXECUTOR.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        ShellSortTask task = new ShellSortTask();
        //task.insertSort()
        //task.shellSort()
        task.pShellSort();
        System.out.println(Arrays.toString(arr));
    }
}