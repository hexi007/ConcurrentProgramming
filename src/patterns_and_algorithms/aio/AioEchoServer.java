package patterns_and_algorithms.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * description AIO 服务器
 * create 2020-11-10 19:50
 *
 * @author 27771
 **/
public class AioEchoServer {

    public final static int PORT = 8000;
    private AsynchronousServerSocketChannel server;

    public AioEchoServer() throws IOException {
        // 绑定端口，使用 AsynchronousServerSocketChannel 作为异步服务器
        this.server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }

    /**
     * 开启服务器
     */
    public void start() {
        System.out.println("server listen on " + PORT);
        // accept() 做了两件事
        // 1.发起 accept 请求，告诉系统可以开始监听端口
        // 2.注册 CompletionHandler 实例，告诉系统一旦有客户端前来连接
        //   如果成功连接，就去执行 completed()
        //   如果连接失败，就去执行 failed()
        // 所以 accept 不会阻塞，它会立即返回
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            final ByteBuffer buffer = ByteBuffer.allocate(1024);

            /**
             * 被执行意味着已有客户端成功连接
             */
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                System.out.println(Thread.currentThread().getName());
                Future<Integer> writeResult = null;

                try {
                    buffer.clear();
                    // read() 是异步的，读取客户数据
                    result.read(buffer).get(100, TimeUnit.SECONDS);
                    buffer.flip();
                    // 数据写回客户端，write() 是异步的，但是 Future 模式立即返回
                    writeResult = result.write(buffer);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // 服务器进行下一个客户端连接的准备，同时关闭正在处理的客户端连接
                        server.accept(null, this);
                        assert writeResult != null;
                        // 在关闭之前确保之前的 write() 操作完成，使用 Future.get() 进行等待
                        writeResult.get();
                        result.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed: " + exc);
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // start() 使用的方法是异步的，会立即返回，不会像阻塞那样进行等待
        new AioEchoServer().start();
        // 等待客户端的到来
        while (true) {
            Thread.sleep(1000);
        }
    }
}