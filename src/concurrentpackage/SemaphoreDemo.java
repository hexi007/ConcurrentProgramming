package concurrentpackage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * description 允许多线程访问：信号量
 * 信号量允许指定多的线程同时访问同一个资源
 *
 * @author 27771
 * create 2020-10-22 19:21
 **/
public class SemaphoreDemo implements Runnable{
    public final Semaphore semaphore = new Semaphore(5);

    @Override
    public void run() {
        try {
            //申请信号量
            semaphore.acquire();
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getId() + " : done");
            //释放信号量
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int threadPoolSize = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        final SemaphoreDemo demo = new SemaphoreDemo();
        for (int i = 0; i < threadPoolSize; i++){
            executorService.submit(demo);
        }
    }
}