package concurrent_package;

import java.util.concurrent.*;

/**
 * description 在线程池中寻找堆栈
 *
 * @author 27771
 * create 2020-10-27 15:01
 **/
public class TraceThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 一个整数除法任务
     */
    public static class DivTask implements Runnable {

        private final int a, b;

        public DivTask(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public void run() {
            int res = a / b;
            System.out.println(res);
        }
    }

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                   TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(wrap(command, clientTrace(), Thread.currentThread().getName()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task, clientTrace(), Thread.currentThread().getName()));
    }

    private Exception clientTrace() {
        return new Exception("Client stack trace");
    }

    /**
     *  将传入的Runnable进行一层包装，使之能处理异常信息。当任务发生异常，这个异常会被打印
     * @param task 传入的Runnable
     * @param clientTrace 处理异常信息
     * @param name 线程名
     * @return  包装好的Runnable
     */
    private Runnable wrap(final Runnable task, final Exception clientTrace, String name) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                clientTrace.printStackTrace();
                throw e;
            }
        };
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pools = new TraceThreadPoolExecutor(0, Integer.MAX_VALUE,
                0L, TimeUnit.SECONDS, new SynchronousQueue<>());
        int taskSize = 5;
        for(int i = 0; i < taskSize; i++){
            //构造一个除零任务
            pools.execute(new DivTask(100, i));
        }
    }
}