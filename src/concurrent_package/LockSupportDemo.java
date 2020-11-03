package concurrent_package;

import java.util.concurrent.locks.LockSupport;

/**
 * description 线程阻塞工具类,在线程内任意位置让线程阻塞
 * LockSupport使用类似信号量的机制，为每个线程准备了一个许可，许可可用，park()立即返回并消费这个许可
 * 如果不可用就会阻塞，unpark()会使得一个许可可用，注意许可不能累加，不可能拥有超过一个许可
 *
 * @author 27771
 * create 2020-10-26 19:16
 **/
public class LockSupportDemo {

    public static Object o = new Object();


    public static class ChangeObjectThread extends Thread {
        public ChangeObjectThread(String name){
            super.setName(name);
        }

        @Override
        public void run() {
            //同意需要包含在对应的synchronized语句中
            synchronized (o) {
                System.out.println("in  " + getName());
                LockSupport.park();
                System.out.println("out " + getName());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ChangeObjectThread t1 = new ChangeObjectThread("t1");
        ChangeObjectThread t2 = new ChangeObjectThread("t2");
        t1.start();
        Thread.sleep(100);
        t2.start();
        LockSupport.unpark(t1);
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
    }
}