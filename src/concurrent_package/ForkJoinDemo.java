package concurrent_package;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * description 分而治之：Fork/Join 框架
 * 使用 Fork/Join 时如果划分层次很深时就可能会出现两种情况：
 * 第一：系统内线程数量越积越多，导致性能严重下降
 * 第二：函数调用层次很深，最终导致栈溢出，比如 THRESHOLD = 1000
 * 此外，Fork/Join 线程池使用一个无锁的栈来管理空闲线程
 * 如果一个工作线程暂时取不到可用任务则可能会被挂起，挂起线程会被压入线程池维护的栈中
 * 将来有可用任务时，再从栈中唤醒这些被挂起的线程
 *
 * @author 27771
 * create 2020-10-27 15:31
 **/
public class ForkJoinDemo {

    /**
     * 定义了任务返回值为Long
     */
    public static class CountTask extends RecursiveTask<Long> {
        //设置任务分解规模
        private static final int THRESHOLD = 10000;
        private final long start, end;

        public CountTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long sum = 0;
            //如果需要计算的总和小于 THRESHOLD 就不需要再细分任务
            boolean cannotCompute = (end - start) < THRESHOLD;
            if(cannotCompute){
                for(long i = start; i < end; i++){
                    sum += i;
                }
            } else {
                //简单的将原有任务划分成 100 个等规模的小任务
                int miniTaskSize = 100;
                long step = (end + start) / miniTaskSize;
                ArrayList<CountTask> subTasks = new ArrayList<>();
                long pos = start;
                for(int i = 0; i < miniTaskSize; i++){
                    long lastOne = pos + step;
                    if(lastOne > end){
                        lastOne = end;
                    }
                    CountTask subTask = new CountTask(pos, lastOne);
                    pos += step + 1;
                    subTasks.add(subTask);
                    //使用 fork 提交子任务
                    subTask.fork();
                }
                for(CountTask countTask : subTasks){
                    //等待所有子任务结束，并将结果再次求和
                    sum += countTask.join();
                }
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //构造一个计算1到200000求和的任务
        CountTask task = new CountTask(0, 200000L);
        //将任务提交到线程池，线程池会返回一个携带结果的任务
        ForkJoinTask<Long> result = forkJoinPool.submit(task);
        try {
            //通过get获取最终结果
            long res = result.get();
            System.out.println("res = " + res);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}