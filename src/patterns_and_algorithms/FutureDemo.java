package patterns_and_algorithms;

import java.util.concurrent.*;

/**
 * description JDK 中的 Future 模式
 * create 2020-11-05 15:50
 *
 * @author 27771
 **/
public class FutureDemo {
    private static class  RealData implements Callable<String> {

        private final String para;

        public RealData(String para) {
            this.para = para;
        }

        @Override
        public String call() {
            StringBuilder sb = new StringBuilder();
            int taskRound = 10;
            for(int i = 0; i < taskRound; i++){
                sb.append(para).append(" ");
                try{
                    //模拟耗时任务
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //构造 FutureTask
        FutureTask<String> future = new FutureTask<>(new RealData("a"));
        ExecutorService executor = Executors.newFixedThreadPool(1);
        //执行 FutureTask，在这里开启线程进行 RealData 的 call() 执行
        executor.submit(future);
        System.out.println("请求完毕");
        System.out.println("是否完成 = " + future.isDone());

        try{
            //模拟一个与 Data 无关的其他业务逻辑
            //处理这个业务逻辑过程中， RealData 被创建，从而充分利用了等待时间
            System.out.println("处理业务逻辑中。。。");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("是否完成 = " + future.isDone());
        System.out.println("数据 = " + future.get());
        executor.shutdown();
    }
}