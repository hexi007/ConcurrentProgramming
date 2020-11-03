package concurrent_package;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description 固定大小的线程池
 * 在实际生产环境中线程数量必须得到控制，盲目的大量创建线程对系统性能是有伤害的
 *
 * @author 27771
 * create 2020-10-26 19:53
 **/
public class ThreadPoolDemo {

    public static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + " : Thread ID : " +
                    Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        MyTask task = new MyTask();
        //创建了固定为5个线程的线程池，安排每个任务要执行一秒钟
        ExecutorService es = Executors.newFixedThreadPool(5);
        //ExecutorService es = Executors.newCachedThreadPool()会略快一些
        int taskSize = 10;
        for(int i = 0; i < taskSize; i++){
            es.submit(task);
        }
    }
}