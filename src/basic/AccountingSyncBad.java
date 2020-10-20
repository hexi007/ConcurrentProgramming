package basic;

/**
 * description synchronized关键字
 * 指定加锁对象：对给定对象加锁，进入同步代码之前要获得给定对象的锁
 * 直接作用于实例方法：相当于对当前实例加锁，进入同步代码前要获得当前实例的锁
 * 直接作用于静态方法：相当于对当前类加锁，进入同步代码前要获得当前类的锁
 *
 * @author 27771
 * create 2020-10-20 18:38
 **/
public class AccountingSyncBad implements Runnable{
    static int i = 0;

    private synchronized void increase() { i++; }

    @Override
    public void run() {
        int rounds = 10000000;
        for(int j = 0; j < rounds; j++){ increase(); }
    }

    public static void main(String[] args) throws InterruptedException {
        AccountingSyncBad accountingSyncBad = new AccountingSyncBad();
        Thread t1 = new Thread(accountingSyncBad);
        Thread t2 = new Thread(accountingSyncBad);
        /*
         * 写成这样是错的 Thread t2 = new Thread(new AccountingSyncBad());
         * 两个线程会指向不同Runnable实例，之后只会加自己的Runnable锁
         * 即两个线程使用不同的锁，线程安全无法保证
         * 可以将increase()方法改写成 private static synchronized void increase() { i++; }
         * 这样方法快请求的是当前类的锁，而不是当前实例
         */

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(i);
    }
}