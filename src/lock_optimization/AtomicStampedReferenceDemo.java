package lock_optimization;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * description 带有时间戳的对象引用 ： AtomicStampedReference
 * AtomicStampedReference 不仅维护了对象值，还维护了一个时间戳
 * 当 AtomicStampedReference 对应值被修改时，除了更新数据本身外，还必须更新时间戳
 * 因此即使对象值被反复读写，写回原值，只要时间戳发生变化，就能防止不恰当的写入
 *
 * @author 27771
 * create 2020-10-31 20:24
 **/
public class AtomicStampedReferenceDemo {
    private static AtomicStampedReference<Integer> money  =
            new AtomicStampedReference<>(19, 0);

    public static void main(String[] args) {
        //模拟多个线程同时更新后头数据库，为用户充值
        int threadSize = 3;
        for(int i = 0; i < threadSize; i++){
            //获得当前时间戳
            final int timeStamp = money.getStamp();
            new Thread(){
                @Override
                public void run() {
                    while (true) {
                        while (true) {
                            //获得当前对象引用
                            Integer m = money.getReference();
                            if(m < 20){
                                //比较设置，参数依次为：期望值，写入新值，期望时间戳，新时间戳
                                if(money.compareAndSet(m , m + 20, timeStamp, timeStamp + 1)) {
                                    System.out.println("余额小于 20 元，充值成功，余额 ： " +
                                            money.getReference() + " 元");
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }.start();
        }

        //用户消费线程，模拟消费行为
        new Thread() {
            @Override
            public void run() {
                int consumeTime = 100;
                for(int i = 0; i < consumeTime; i++){
                    while (true) {
                        int timeStamp = money.getStamp();
                        Integer m = money.getReference();
                        if(m > 10){
                            System.out.println("余额大于 10 元");
                            if(money.compareAndSet(m, m - 10, timeStamp, timeStamp + 1)) {
                                System.out.println("成功消费 10 元，余额 ： " + money.getReference());
                                break;
                            }
                        } else {
                            System.out.println("余额不够");
                            break;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}