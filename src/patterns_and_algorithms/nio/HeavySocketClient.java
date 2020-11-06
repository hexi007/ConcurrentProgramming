package patterns_and_algorithms.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * description 让 CPU 进行 I/O 等待的客户端
 * 在本例中服务器总在等待 IO ，Echo 服务器处理大量这样的请求会拖慢服务器的速度
 * create 2020-11-06 19:44
 *
 * @author 27771
 **/
public class HeavySocketClient {

    private static final ExecutorService TP = Executors.newCachedThreadPool();
    private static final int SLEEP_TIME = 1000 * 1000 * 1000;

    public static class EchoClient implements Runnable {

        @Override
        public void run() {
            Socket client = null;
            PrintWriter writer = null;
            BufferedReader reader = null;

            try {
                client = new Socket();
                // 设置 Socket
                client.connect(new InetSocketAddress("localhost", 8000));
                writer = new PrintWriter(client.getOutputStream(), true);
                writer.print("H");
                // 每次都停顿一段时间
                LockSupport.parkNanos(SLEEP_TIME);
                writer.print("E");
                LockSupport.parkNanos(SLEEP_TIME);
                writer.print("L");
                LockSupport.parkNanos(SLEEP_TIME);
                writer.print("L");
                LockSupport.parkNanos(SLEEP_TIME);
                writer.print("O");
                LockSupport.parkNanos(SLEEP_TIME);
                writer.println();
                writer.flush();

                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                System.out.println("from server : " + reader.readLine());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(writer != null) {
                        writer.close();
                    }
                    if(reader != null) {
                        reader.close();
                    }
                    if(client != null) {
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        EchoClient client = new EchoClient();
        int roundTimes = 10;
        for(int i= 0; i < roundTimes; i++){
            TP.execute(client);
        }
    }
}