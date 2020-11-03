package concurrent_basic;

/**
 * description 错误的加锁
 *
 * @author 27771
 * create 2020-10-21 14:49
 **/
public class BadLockOnInteger implements Runnable{
    private static Integer i = 0;

    public static BadLockOnInteger instance = new BadLockOnInteger();

    @Override
    public void run() {
        int rounds = 10000000;
        for (int j = 0; j < rounds;j++){
            //给Integer加锁会出问题，因为i++实质是Integer.valueOf(i.intValue() + 1)
            //多个线程不一定能看到同一个i对象，所以两个线程每次加锁可能加在不同对象实例上
            //Reports synchronized statements where the lock expression is a reference to a non-final field.
            //Such statements are unlikely to have useful semantics,
            //as different threads may be locking on different objects even when operating on the same object.
            //可以改为synchronized instance)
            synchronized (i){
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(i);
    }
}