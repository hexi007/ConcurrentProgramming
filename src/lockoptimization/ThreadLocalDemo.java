package lockoptimization;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description 人手一只笔——线程局部变量
 *
 * @author 27771
 * create 2020-10-27 20:35
 **/
public class ThreadLocalDemo {
    /**
     * 使用 ThreadLocal 为每一个线程都产生一个 SimpleDateFormat 对象实例
     * SimpleDateFormat.parse不是线程安全的
     */
    private static final ThreadLocal<SimpleDateFormat> TL = new ThreadLocal<>();

    public static class ParseDate implements Runnable {

        private final int i;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                //如果当前线程不持有 SimpleDateFormat 对象实例，就新建一个并把它设置到当前线程中
                //如果已经持有，则直接使用
                if(TL.get() == null){
                    TL.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                }
                Date t = TL.get().parse("2020-10-27 20:38:" + i % 60);
                System.out.println(i + " : " + t);
                //在线程池场景下，线程经常会被复用，如果不清理自定义的 ThreadLocal 变量，可能会影响后续业务逻辑
                TL.remove();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        int parseDateTimes = 1000;
        for(int i = 0; i < parseDateTimes; i++){
            es.execute(new ParseDate(i));
        }
        es.shutdown();
    }
}