package patterns_and_algorithms;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * Description: disruptor 生产者和消费者
 * create 2020-11-03 20:38
 *
 * @author 27771
 **/
public class DisruptorDemo {

    public static class PcData {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class PcDataFactory implements EventFactory<PcData> {

        @Override
        public PcData newInstance() {
            return new PcData();
        }
    }

    public static class Consumer implements WorkHandler<PcData> {

        @Override
        public void onEvent(PcData pcData) throws Exception {
            System.out.println(Thread.currentThread().getId() + " Event : -- " +
                    pcData.getValue() * pcData.getValue() + " --");
        }
    }

    public static class Producer {
        private final RingBuffer<PcData> ringBuffer;

        public Producer(RingBuffer<PcData> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        public void pushData(int value) {
            long sequence = ringBuffer.next();
            try {
                PcData pcData = ringBuffer.get(sequence);
                pcData.setValue(value);
            } finally {
                ringBuffer.publish(sequence);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Executor es = Executors.newCachedThreadPool();

        PcDataFactory factory = new PcDataFactory();
        int bufferSize = 1024;

        Disruptor<PcData> disruptor = new Disruptor<>(factory,
                bufferSize,
                es,
                ProducerType.MULTI,
                new BlockingWaitStrategy()
        );

        disruptor.handleEventsWithWorkerPool(
                new Consumer(),
                new Consumer(),
                new Consumer(),
                new Consumer()
        );

        disruptor.start();

        RingBuffer<PcData> ringBuffer = disruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);

        for(int l = 0; l < 1000; l++) {
            producer.pushData(l);
            Thread.sleep(100);
            System.out.println("add data " + (byte)l);
        }
    }
}