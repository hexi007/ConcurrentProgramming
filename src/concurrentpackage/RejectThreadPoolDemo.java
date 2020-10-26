package concurrentpackage;

import java.util.concurrent.*;

/**
 * description 自定义线程池和拒绝策略的使用
 *
 * @author 27771
 * create 2020-10-26 20:44
 **/
public class RejectThreadPoolDemo {
    public static class MyTask implements Runnable {

        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + " : Threads Name ； " +
                    Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask myTask = new MyTask();
        ExecutorService es = new ThreadPoolExecutor(
                //指定线程池中线程数量和最大线程数量
                5, 5,
                //多余线程存活时间和该时间的单位
                0L, TimeUnit.MILLISECONDS,
                //设置了界限的无界任务队列
                new LinkedBlockingQueue<Runnable>(10),
                //默认线程工厂
                Executors.defaultThreadFactory(),
                //自定义拒绝策略
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println(r.toString() + " is discard");
                    }
                });
        for(int i = 0; i < Integer.MAX_VALUE; i++){
            es.submit(myTask);
            Thread.sleep(10);
        }
    }
}