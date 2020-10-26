package concurrentpackage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description 拓展线程池
 *
 * @author 27771
 * create 2020-10-26 21:08
 **/
public class ExtThreadPool {
    public static class MyTask implements Runnable {
        public String threadName;

        public MyTask(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            System.out.println("正在执行 " + " Thread ID : " + Thread.currentThread().getId() +
                    " , Task Name = " + threadName);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>() ) {
            //拓展 beforeExecute 、 afterExecute 和 terminated
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("准备执行 ： " + ((MyTask) r).threadName);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("执行完成 ： " + ((MyTask) r).threadName);
            }

            @Override
            protected void terminated() {
                System.out.println("线程池退出");
            }
        };
        int taskSize = 5;
        for(int i = 0; i < taskSize; i++){
            MyTask task = new MyTask("TASK-" + i);
            es.execute(task);
            Thread.sleep(10);
        }
        //优雅的关闭线程池，等待所有任务执行完后关闭线程池
        //可以理解为shutdown只是发送了一个关闭信号而已，但之后不能再接受新的任务了
        es.shutdown();
    }
}