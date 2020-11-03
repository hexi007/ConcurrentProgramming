package lock_optimization;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * description 让普通变量也享受原子操作： AtomicIntegerFieldUpdater
 * 在不改动（或者极少改动）原有代码基础上，让普通变量也享受 CAS 操作带来的线程安全性 。<br/>
 * 注意事项：<br/>
 * 1.Updater 只能修改可见范围内的变量，因为是使用反射获得这个变量， score 申明为 private 会出错.<br/>
 * 2.为了确保变量被正确读写，它必须是 volatile 类型的。<br/>
 * 3.CSA 操作会通过对象实例中的偏移量进行直接赋值，因此不支持 static 字段。<br/>
 * create 2020-11-02 20:50
 *
 * @author 27771
 **/
public class AtomicIntegerFieldUpdaterDemo {
    private static class Candidate {
        volatile int score;
    }

    /**
     * 定义了 AtomicIntegerFieldUpdater 实例，用来对 Candidate.score 进行写入
     */
    public final static AtomicIntegerFieldUpdater<Candidate> SCORE_UPDATER
            = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");
    /**
     * 检查 Updater 是否正常
     */
    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate candidate = new Candidate();
        int threadSize = 10000;
        Thread[] threads = new Thread[threadSize];
        for(int i = 0; i < threadSize; i++){
            threads[i] = new Thread() {
                @Override
                public void run() {
                    if(Math.random() > 0.4){
                        //使用 Updater 修改 Candidate.score 值
                        SCORE_UPDATER.incrementAndGet(candidate);
                        //作为参考依据
                        allScore.incrementAndGet();
                    }
                }
            };
            threads[i].start();
        }
        for(int i = 0; i < threadSize; i++){
            threads[i].join();
        }
        System.out.println("score = " + candidate.score);
        System.out.println("allScore = " + allScore);
    }
}