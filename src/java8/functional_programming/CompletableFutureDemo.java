package java8.functional_programming;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
* description CompletableFuture 工具类
* @author 27771
* create 2020-11-14 20:42
**/
public class CompletableFutureDemo {

    static class AskThread implements Runnable {

        /**
         * CompletableFuture 和 future 一样可以作为调用契约
         * 如果数据还没准备好，线程就会等待
         * 通过 CompletableFuture 可以手动设置 CompletableFuture 的完成状态
         */
        CompletableFuture<Integer> re;

        public AskThread(CompletableFuture<Integer> re) {
            this.re = re;
        }

        @Override
        public void run() {
            int myRe = 0;
            try {
                // 计算 CompletableFuture 表示的数字的平方
                // 因为一开始 CompletableFuture 没有数据，所以处于未完成状态
                myRe = re.get() * re.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println(myRe);
        }
    }

    static void testCompletableFuture() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        new Thread(new AskThread(future)).start();
        try {
            // 模拟长时间的计算过程
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 将数据载入 CompletableFuture ，并标记为完成状态
        future.complete(60);
    }

    static Integer calc(Integer para) {
        try {
            // 模拟耗时操作
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 100 / para;
    }

    /**
     * 实现 Future 模式的异步调用
     */
    static void testSupplyAsync() {
        // supplyAsync() 函数中会在一个新线程执行传入的参数，会理解返回
        // 获取 calc() 的计算结果时，如果当前计算未完成，get() 方法的线程就会等待
        // future 就是调用的契约，用于获取最终结果
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> calc(10));
        try  {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    static void streamingCall() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(20))
                // 连续使用流式调用对任务结果处理再加工
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        try  {
            // get() 方法目的是等待 calc() 函数执行完成
            // 如果不执行这个调用，由于 CompletableFuture 的异步执行
            // 主函数不等 calc() 完毕就会退出，
            // 随着主线程结束，所有 Daemon 线程会立即退出，从而导致 calc() 方法无法正常完成
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    static void handleExceptionally() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(0))
                // 如果没有异常发生，则 CompletableFuture 返回原有结果
                // 遇到异常则再 exceptionally() 中处理异常并返回默认值的给接下来的流
                .exceptionally(ex -> {
                    System.out.println(ex.toString());
                    return 0;
                })
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        try  {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    static void composeOrCombineCompletableFuture() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> calc(5))
                // 将处理的结果传给 thenCompose ，进一步传给新生成的 CompletableFuture 实例
                .thenCompose((i) -> CompletableFuture.supplyAsync(() -> calc(i)))
                .thenApply((str) -> "\"" + str + "\"").thenAccept(System.out::println);
        try  {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        CompletableFuture<Integer> futureOne = CompletableFuture.supplyAsync(() -> calc(5));
        CompletableFuture<Integer> futureTwo = CompletableFuture.supplyAsync(() -> calc(20));
        // thenCombine() 组合两个 CompletableFuture 实例，将结果相乘
        CompletableFuture<Void> futureRes = futureOne.thenCombine(futureTwo, (i, j) -> i * j)
                .thenApply((str) -> "\"" + str + "\"").thenAccept(System.out::println);
        try  {
            futureRes.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //testCompletableFuture();
        //testSupplyAsync();
        //streamingCall();
        //handleExceptionally();
        composeOrCombineCompletableFuture();
    }
}