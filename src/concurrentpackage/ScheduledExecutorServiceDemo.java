package concurrentpackage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * description 计划任务
 *
 * @author 27771
 * create 2020-10-26 20:24
 **/
public class ScheduledExecutorServiceDemo {

    public static void main(String[] args) {
        ThreadFactory threadFactory;
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(System.currentTimeMillis() / 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
        //正常任务执行间隔是2秒钟，但如果任务执行时间太长，则下一个任务会在上个任务刚结束时就立刻执行
        //如果任务遇到异常，那么所有子任务都会停止调度
        //必须保证异常能被及时处理，为周期性任务的稳定提供稳定调度条件
    }
}