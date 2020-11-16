package java8;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.StampedLock;

/**
 * description 读写锁的改进：StampedLock
 * StampedLock 通过引入乐观读增加系统的并行度
 *
 * @author 27771
 * create 2020-11-16 20:14
 **/
public class StampedLockDemo {

    static class Point {
        double x, y;
        final StampedLock sl = new StampedLock();

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        void move(double deltaX, double deltaY) {
            long stamp = sl.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                sl.unlockWrite(stamp);
            }
        }

        double distanceFromOrigin() {
            // 首先进行乐观读， stamp 作为这次锁的一个凭证
            long stamp = sl.tryOptimisticRead();
            // 读取的 currentX，currentY 不确定是否与 x,y 一致
            double currentX = x, currentY = y;
            // validate() 判断读过程中 stamp 是否被修改过
            if (!sl.validate(stamp)) {
                // 出现了脏读，可以使用 CAS 一样一直使用乐观读
                // 也可以将乐观锁变为悲观锁
                stamp = sl.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    sl.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
    }

    static void basicUse() {
        Point point = new Point();
        new Thread(() -> {
            int rounds = 100;
            while (rounds-- > 0) {
                point.move(Math.random(), Math.random());
            }
            System.out.println(point);
        }).start();
        new Thread(() -> {
            int rounds = 1000;
            while (rounds-- > 0) {
                double distance = point.distanceFromOrigin();
                System.out.println(distance);
            }
        }).start();
    }

    static class HoldCpuReadThread implements Runnable {

        StampedLock lock;

        public HoldCpuReadThread(StampedLock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            long lockRead = lock.readLock();
            System.out.println(Thread.currentThread().getName() + " 获得锁");
            lock.unlockRead(lockRead);
        }
    }

    /**
     * StampedLock 内部使用的 Unsafe.park() 函数，该函数会在遇到线程中断时直接返回
     * StampedLock 没有处理关中断的逻辑，所以 park() 函数被中断后会再次进入循环
     * 而当推出条件不满足时就会疯狂占用 CPU
     */
    static void wrongUse() throws InterruptedException {
        Thread[] holdCpuThreads = new Thread[3];
        StampedLock lock = new StampedLock();

        new Thread() {
            @Override
            public void run() {
                // 线程先占用写锁
                long readLong = lock.writeLock();
                // 先不释放一直等待
                LockSupport.parkNanos(6000_0000_0000L);
                lock.unlockWrite(readLong);
            }
        }.start();
        Thread.sleep(100);
        for (int i = 0; i < holdCpuThreads.length; i++) {
            // 因为写锁所有读线程会被挂起
            holdCpuThreads[i] = new Thread(new HoldCpuReadThread(lock));
            holdCpuThreads[i].start();
        }
        Thread.sleep(1_0000);
        for (int i = 0; i < holdCpuThreads.length; i++) {
            // 线程中断后，中断导致了 park() 函数的返回，使线程再一次进入了运行状态
            holdCpuThreads[i].interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //basicUse();
        wrongUse();
    }
}