package lock_optimization;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description ThreadLocal 回收
 * 第二次 GC 后第一次创建的 SimpleDateFormat 子类实例会被全部回收
 *
 * @author 27771
 * create 2020-10-28 16:17
 **/
public class ThreadLocalDemoGc {
    static volatile ThreadLocal<SimpleDateFormat> tl = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected void finalize() throws Throwable {
            System.out.println(this.toString() + " is gc");
            super.finalize();
        }
    };

    static volatile CountDownLatch cd = new CountDownLatch(1000);

    public static class ParseDate implements Runnable {
        private final int i;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                if(tl.get() == null){
                    tl.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {
                        @Override
                        protected void finalize() throws Throwable {
                            System.out.println(this.toString() + " is gc");
                            super.finalize();
                        }
                    });
                    System.out.println(Thread.currentThread().getId() +
                            " : create SimpleDateFormat");
                }
                Date t = tl.get().parse("2020-10-28 16:28:" + i % 60);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cd.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        int taskSize = 1000;
        for(int i = 0; i < taskSize; i++){
            es.execute(new ParseDate(i));
        }
        cd.await();
        System.out.println("Mission complete!!");
        //失去强引用
        tl = null;
        //强制回收
        System.gc();
        System.out.println("First GC complete");

        //在设置 ThreadLocal 的时候，会清除 ThreadLocal 中的无效对象
        //将新的变量添加进 ThreadLocalMap 中就会自动进行一次清理
        //虽然不一定会进行彻底扫描，但调用 System.gc() 会彻底扫描
        tl = new ThreadLocal<>();
        cd = new CountDownLatch(1000);
        for(int i = 0; i < taskSize; i++){
            es.execute(new ParseDate(i));
        }
        cd.await();
        System.out.println("Mission complete!!");
        Thread.sleep(1000);
        System.gc();
        System.out.println("Second GC complete");

    }
}