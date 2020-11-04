package patterns_and_algorithms;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: disruptor 生产者和消费者
 * create 2020-11-03 20:38
 *
 * @author 27771
 **/
public class DisruptorDemo {

    private static final long TASK_SIZE = 20_0000_0000;

    /**
     *  生产任务，或者相关任务的数据
     */
    public static class PcData {
        private long value;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }

    /**
     *  PcData 工厂类，在 disruptor 初始化时构造所有的缓冲区对象实例
     */
    public static class PcDataFactory implements EventFactory<PcData> {

        @Override
        public PcData newInstance() {
            return new PcData();
        }
    }

    /**
     *  消费者从 ringBuffer 中获取 PcData
     */
    public static class Consumer implements WorkHandler<PcData> {
        private static final int SLEEP_TIME = 100;

        @Override
        public void onEvent(PcData pcData) throws Exception {
            long res = pcData.getValue() * pcData.getValue();
//            System.out.println(MessageFormat.format("consume result :  {0} * {1} = {2}",
//                    pcData.getValue(), pcData.getValue(), res));
            //Thread.sleep(SLEEP_TIME);
        }
    }

    /**
     *  生产者负责创建 PcData 对象并将其加入 ringBuffer 中
     */
    public static class Producer implements Runnable {
        private volatile boolean isRunning = true;
        private static AtomicLong count = new AtomicLong();
        private static final int SLEEP_TIME = 100;
        private final RingBuffer<PcData> ringBuffer;

        public Producer(RingBuffer<PcData> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        @Override
        public void run() {
            while (isRunning && count.get() < TASK_SIZE) {
                long sequence = ringBuffer.next();
                try {
                    PcData pcData = ringBuffer.get(sequence);
                    pcData.setValue(count.incrementAndGet());
                    //System.out.println(pcData.getValue() + " is put into ringBuffer");
                    //Thread.sleep(SLEEP_TIME);
                } finally {
                    ringBuffer.publish(sequence);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        PcDataFactory factory = new PcDataFactory();
        int bufferSize = 4096;

        Disruptor<PcData> disruptor = new Disruptor<>(factory,
                bufferSize,
                es,
                ProducerType.MULTI,
                new BlockingWaitStrategy()
        );

        disruptor.handleEventsWithWorkerPool(
                new Consumer(),
                new Consumer(),
                new Consumer()
        );

        disruptor.start();

        RingBuffer<PcData> ringBuffer = disruptor.getRingBuffer();

        long startTime = System.currentTimeMillis();

        Thread producer1 = new Thread(new Producer(ringBuffer));
        Thread producer2 = new Thread(new Producer(ringBuffer));
        Thread producer3 = new Thread(new Producer(ringBuffer));
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