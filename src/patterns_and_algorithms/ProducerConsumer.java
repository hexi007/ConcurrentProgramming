package patterns_and_algorithms;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * description 生产者消费者模式 <br/>
 * 生产者和消费者模式对生产者和消费者线程进行解耦，优化了整体结构。 <br/>
 * 由于缓冲区的存在，允许生产者线程和消费者线程在执行性能上的性能差异 <br/>
 * 一定程度缓解了性能瓶颈对系统性能的影响 <br/>
 * 使用 ConcurrentLinkedQueue 可以提升性能，因为它使用了大量 CAS 操作 <br/>
 * create 2020-11-03 19:23
 *
 * @author 27771
 **/
public class ProducerConsumer {

    private static final long TASK_SIZE = 20_0000_0000;

    /**
     *  BlockingQueue 作为共享内存缓冲区维护任务或数据队列
     */
    private static final BlockingQueue<PcData> QUEUE =
            new LinkedBlockingQueue<>(10);

    /**
     *  生产任务，或者相关任务的数据
     */
    private final static class PcData {
        //数据
        private final long intData;

        private PcData(long intData) {
            this.intData = intData;
        }

        public long getData() {
            return intData;
        }

        @Override
        public String toString() {
            return "PcData{" +
                    "intData=" + intData +
                    '}';
        }
    }

    /**
     *  生产者负责创建 PcData 对象并将其加入 BlockingQueue 中
     */
    private static class Producer implements Runnable {

        private volatile boolean isRunning = true;
        //总数，原子操作
        private static AtomicLong count = new AtomicLong();
        private static final int SLEEP_TIME = 100;

        @Override
        public void run() {
            try {
                while (isRunning  && count.get() < TASK_SIZE) {
                    PcData pcData = new PcData(count.incrementAndGet());
                    //System.out.println(pcData.getData() + " is put into queue");
                    //提交任务到内存缓冲区
                    if(!QUEUE.offer(pcData, 2 , TimeUnit.MILLISECONDS)){
                        //System.err.println("failed to put data : " + pcData);
                    }
                    //Thread.sleep(SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        public void stop() {
            isRunning = false;
        }
    }

    /**
     *  消费者从 BlockingQueue 中获取 PcData
     */
    private static class Consumer implements Runnable {

        private volatile boolean isRunning = true;
        private static final int SLEEP_TIME = 100;

        @Override
        public void run() {
            try {
                while (isRunning) {
                    //从内存缓冲区提取任务
                    PcData pcData = QUEUE.take();
                    //处理任务
                    long res = pcData.getData() * pcData.getData();
//                    System.out.println(MessageFormat.format("consume result :  {0} * {1} = {2}",
//                            pcData.getData(), pcData.getData(), res));
                    //Thread.sleep(SLEEP_TIME);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        public void stop() {
            isRunning = false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //建立消费者
        Consumer consumer1 = new Consumer();
        Consumer consumer2 = new Consumer();
        Consumer consumer3 = new Consumer();

        //建立线程池
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(consumer1);
        service.execute(consumer2);
        service.execute(consumer3);

        long startTime = System.currentTimeMillis();

        Thread producer1 = new Thread(new Producer());
        Thread producer2 = new Thread(new Producer());
        Thread producer3 = new Thread(new Producer());
        producer1.start();
        producer2.start();
        producer3.start();
        producer1.join();
        producer2.join();
        producer3.join();

        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + " ms");
    }
}