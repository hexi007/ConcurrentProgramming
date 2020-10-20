package basic;

import java.util.HashMap;
import java.util.Map;

/**
 * description HashMap不是线程安全容器
 *
 * @author 27771
 * create 2020-10-20 19:26
 **/
public class HashMapMultiThread {
    static Map<String, String> map = new HashMap<>();

    private static class AddThread implements Runnable {

        private int start = 0;

        public AddThread(int start) {
            this.start = start;
        }

        @Override
        public void run() {
            int rounds = 100000, step = 2;
            //不管start设置为0还是1，都只会执行循环50000次
            for(int i = start; i < rounds; i += step){
                map.put(Integer.toString(i), Integer.toBinaryString(i));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new HashMapMultiThread.AddThread(0));
        Thread t2 = new Thread(new HashMapMultiThread.AddThread(1));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        /*
         * 三种结果：
         * 1.程序输出100000，即使并行设计有问题，也未必会表现出来
         * 2.输出一个比100000小的数字，出现了两个线程对HashMap同一个位置赋值的情况
         * 3.程序永远无法结束，在jdk7k及之前，链表可能被破坏成环
         * 可以使用线程安全的ConcurrentHashMap
         */

        System.out.println(map.size());
    }
}