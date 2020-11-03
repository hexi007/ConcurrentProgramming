package lock_optimization;

import java.util.Random;
import java.util.concurrent.*;

/**
 * description ThreadLocal 对性能的帮助
 *
 * @author 27771
 * create 2020-10-31 15:21
 **/
public class ThreadLocalRandomTest {

    public static final int TASK_COUNT = 1000_0000;
    public static final int THREAD_COUNT = 4;
    private static final ExecutorService EXE = Executors.newFixedThreadPool(THREAD_COUNT);

    public static Random rnd = new Random(123);
    /**
     * 由 ThreadLocal 封装的 Random
     */
    public static ThreadLocal<Random> tRnd = ThreadLocal.withInitial(() -> new Random(123));

    public static class RndTask implements Callable<Long> {

        /**
         * MODE 为 0 表共享一个 Random ，为 1 表示为各线程分配一个 Random
         */
        private final int MODE;

        public RndTask(int mode) {
            this.MODE = mode;
        }

        public Random getRnd() {
            if (MODE == 0) {
                return rnd;
            } else if (MODE == 1) {
                return tRnd.get();
            } else {
                return null;
            }
        }

        /**
         * 线程工作内容
         */
        @Override
        public Long call() {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < TASK_COUNT; i++) {
                getRnd().nextInt();
            }
            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + " spend " + (endTime - startTime) +
                     " ms");
            return endTime - startTime;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Future<Long>[] ret = new Future[THREAD_COUNT];

        //MODE 为 0 的模式
        for (int i = 0; i < THREAD_COUNT; i++) {
            ret[i] = EXE.submit(new RndTask(0));
        }
        long totalTime = 0;
        for (int i = 0; i < THREAD_COUNT; i++) {
            totalTime += ret[i].get();
        }
        System.out.println("多线程访问同一个 Random 实例平均时间 ： " + totalTime  / THREAD_COUNT + " ms");

        //MODE 为 1 的模式
        for (int i = 0; i < THREAD_COUNT; i++) {
            ret[i] = EXE.submit(new RndTask(1));
        }
        long totalTime1 = 0;
        for (int i = 0; i < THREAD_COUNT; i++) {
            totalTime1 += ret[i].get();
        }
        System.out.println("使用 ThreadLocal 包装 Random 实例平均时间 ： " + totalTime1  / THREAD_COUNT + " ms");
        System.out.println("使用 ThreadLocal 包装 比共享同一个 Random 快 " + totalTime / totalTime1 + " 倍");
        EXE.shutdown();
    }
}