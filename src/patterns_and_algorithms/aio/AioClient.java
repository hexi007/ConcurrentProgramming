package patterns_and_algorithms.aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * description AIO 客户端
 * create 2020-11-10 20:29
 *
 * @author 27771
 **/
public class AioClient {

    public static void main(String[] args) throws Exception {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 8000), n);
    }
}