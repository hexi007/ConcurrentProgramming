package concurrent_basic;

import java.util.ArrayList;

/**
 * description ArrayList不是线程安全的容器
 *
 * @author 27771
 * create 2020-10-20 19:08
 **/
public class ArrayListMultiThread {
    private static ArrayList<Integer> arrayList = new ArrayList<>(10);

    public static class AddThread implements Runnable {
        @Override
        public void run() {
            int rounds = 1000000;
            for(int i = 0; i < rounds; i++) { arrayList.add(i); }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddThread());
        Thread t2 = new Thread(new AddThread());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        /*
         * 三种结果：
         * 1.程序输出2000000，即使并行设计有问题，也未必会表现出来
         * 2.抛出数组越界异常，ArrayList扩容时没有加锁另一个线程访问了不一致状态
         * 3.输出一个比2000000小的数字，出现了两个线程对ArrayList同一个位置赋值的情况
         * 可以使用线程安全的Vector
         */
        System.out.println(arrayList.size());
    }
}