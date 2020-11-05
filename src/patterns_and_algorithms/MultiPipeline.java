package patterns_and_algorithms;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * description 并行流水线,计算 ( I + J ) * I / 2 <br/>
 * 首先将计算过程拆分为三个步骤： <br/>
 * Plus     : J = I + J <br/>
 * MultiPly : I = I * J <br/>
 * Div      : I = I / 2 <br/>
 *
 * create 2020-11-05 16:10
 *
 * @author 27771
 **/
public class MultiPipeline {
    public static class Msg {
        public double i;
        public double j;
        public String orgStr;

        public Msg(double i, double j) {
            this.i = i;
            this.j = j;
            this.orgStr = "(( " + i + " + " + j + " ） * " + i +
                    " ) / 2";
        }
    }

    /**
     * Plus : J = I + J
     */
    public static class Plus implements Runnable {
        public static BlockingQueue<Msg> blockingQueue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            while (true) {
                try{
                    Msg msg = blockingQueue.take();
                    msg.j = msg.i + msg.j;
                    //传递给乘法线程
                    MultiPly.blockingQueue.add(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * MultiPly : I = I * J
     */
    private static class MultiPly implements Runnable {
        public static BlockingQueue<Msg> blockingQueue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            while (true) {
                try{
                    Msg msg = blockingQueue.take();
                    msg.i = msg.i * msg.j;
                    //传递给除法线程
                    Div.blockingQueue.add(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Div : I = I / 2
     */
    private static class Div implements Runnable {
        public static BlockingQueue<Msg> blockingQueue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            while (true) {
                try{
                    Msg msg = blockingQueue.take();
                    msg.i = msg.i / 2;
                    //输出结果
                    System.out.println(msg.orgStr + " = " + msg.i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new Plus()).start();
        new Thread(new MultiPly()).start();
        new Thread(new Div()).start();

        int iEnd = 10, jEnd = 10;
        for(int i = 1; i <= iEnd; i++){
            for(int j = 1; j <= jEnd; j++){
                Msg msg = new Msg(i, j);
                Plus.blockingQueue.add(msg);
            }
        }
    }
}