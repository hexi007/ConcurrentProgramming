package concurrent_package;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description 倒计时器
 * 简单理解为把门锁起来，不让里面的线程跑出来，用于控制线程等待
 * 让某一个线程等待直到倒计时结束，在开始执行
 *
 * @author 27771
 * create 2020-10-22 20:28
 **/
public class CountDownLatchDemo implements Runnable{

    /**
     * CountDownLatch接受的参数就是这个计时器的计数个数
     */
    private static final CountDownLatch end = new CountDownLatch(10);
    private static final CountDownLatchDemo demo = new CountDownLatchDemo();

    @Override
    public void run() {
        try{
            //模拟检查任务
            Thread.sleep(new Random().nextInt(10) * 1000);
            System.out.println(Thread.currentThread().getName() + " check complete");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //通知倒计时减一
            end.countDown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int nThreads = 10;
        ExecutorService exec = Executors.newFixedThreadPool(nThreads);
        for(int i = 0; i < nThreads; i++){
            exec.submit(demo);
        }

        //等待检查
        end.await();
        //所有检查完成
        System.out.println("All complete.....Fire!");
        exec.shutdown();
    }
}