package concurrentpackage;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * description 循环栅栏
 * cyclic意味循环，即这个计数器可以反复使用
 * public CyclicBarrier(int parties, Runnable barrierAction)
 * parties表示计数总和，barrierAction就是当计数完成后，系统会执行的动作
 *
 * @author 27771
 * create 2020-10-26 16:30
 **/
public class CyclicBarrierDemo {

    public static class Soldier implements Runnable {

        private String soldierName;
        private final CyclicBarrier cyclic;

        public Soldier(String soldierName, CyclicBarrier cyclic) {
            this.soldierName = soldierName;
            this.cyclic = cyclic;
        }

        @Override
        public void run() {
            try{
                //等待所有士兵到齐
                cyclic.await();
                //等待所有士兵完成工作
                doWork();
                //进行下一轮计数，监控是否所有士兵都已经完成了任务
                cyclic.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        private void doWork() {
            try {
                Thread.sleep(Math.abs((new Random().nextInt() % 10000)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(soldierName + " : 任务完成");
        }
    }

    public static class BarrierRun implements Runnable {

        boolean flag;
        int N;

        public BarrierRun(boolean flag, int n) {
            this.flag = flag;
            this.N = n;
        }

        @Override
        public void run() {
            if(flag) {
                System.out.println("司令 ： [士兵 " + N + " 个，任务完成]");
            } else {
                System.out.println("司令 ： [士兵 " + N + " 个，集合完毕]");
                flag = true;
            }
        }
    }

    public static void main(String[] args) {
        final int N = 10;
        Thread[] allSoldier = new Thread[N];
        boolean flag = false;
        //设置屏障点，主要是为了执行这个方法
        CyclicBarrier cyclic = new CyclicBarrier(N, new BarrierRun(flag, N));
        System.out.println("集合队伍");
        for(int i = 0; i < N; i++){
            System.out.println("士兵 " + i + "报道");
            allSoldier[i] = new Thread(new Soldier("士兵 " + i, cyclic));
            allSoldier[i].start();
        }
    }
}