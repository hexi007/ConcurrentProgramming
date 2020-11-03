package patterns_and_algorithms;

import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     *  BlockingQueue 作为共享内存缓冲区维护任务或数据队列
     */
    private static final BlockingQueue<PcData> QUEUE =
            new LinkedBlockingDeque<>(10);

    /**
     *  生产任务，或者相关任务的数据
     */
    private final static class PcData {
        //数据
        private final int intData;

        private PcData(int intData) {
            this.intData = intData;
        }

        public int getData() {
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
        private static AtomicInteger count = new AtomicInteger();
        private static final int SLEEP_TIME = 1000;

        @Override
        public void run() {
            PcData pcData = null;
            Random r = new Random();

            System.out.println("start producer id = " + Thread.currentThread().getId());
            try {
                while (isRunning) {
                    Thread.sleep(r.nextInt(SLEEP_TIME));
                    //构造任务队列
                    pcData = new PcData(count.incrementAndGet());
                    System.out.println(pcData + " is put into queue");
                    //提交任务到内存缓冲区
                    if(!QUEUE.offer(pcData, 2 , TimeUnit.MILLISECONDS)){
                        System.err.println("failed to put data : " + pcData);
                    }
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
        private static final int SLEEP_TIME = 1000;

        @Override
        public void run() {
            System.out.println("start consumer id = " +
                    Thread.currentThread().getId());
            Random r = new Random();
            try {
                while (isRunning) {
                    //从内存缓冲区提取任务
                    PcData pcData = QUEUE.take();
                    //处理任务
                    int res = pcData.getData() * pcData.getData();
                    System.out.println(MessageFormat.format("consume result :  {0} * {1} = {2}",
                            pcData.getData(), pcData.getData(), res));
                    Thread.sleep(r.nextInt(SLEEP_TIME));
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
        //建立生产者和消费者
        Producer producer1 = new Producer();
        Producer producer2 = new Producer();
        Producer producer3 = new Producer();

        Consumer consumer1 = new Consumer();
        Consumer consumer2 = new Consumer();
        Consumer consumer3 = new Consumer();

        //建立线程池
        ExecutorService service = Executors.newCachedThreadPool();
        //允许生产者和消费者
        service.execute(producer1);
        service.execute(producer2);
        service.execute(producer3);
        service.execute(consumer1);
        service.execute(consumer2);
        service.execute(consumer3);

        Thread.sleep(10 * 1000);

        //停止生产者和消费者
        producer1.stop();
        producer2.stop();
        producer3.stop();
        consumer1.stop();
        consumer2.stop();
        consumer3.stop();

        service.shutdown();
    }
}