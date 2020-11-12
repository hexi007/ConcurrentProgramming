package patterns_and_algorithms.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * description AIO 客户端
 * create 2020-11-10 20:29
 *
 * @author 27771
 **/
public class AioClient {

    public static void main(String[] args) throws Exception {
        // 打开 AsynchronousSocketChannel 通道
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        // CompletionHandler 操作都是异步的
        client.connect(new InetSocketAddress("localhost", 8000), null,
                new CompletionHandler<Void, Object>() {
                    @Override
                    public void completed(Void result, Object attachment) {
                        // 数据写入
                        client.write(ByteBuffer.wrap("hello!".getBytes()), null,
                                new CompletionHandler<Integer, Object>() {
                                    @Override
                                    public void completed(Integer result, Object attachment) {
                                        try {
                                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                                            // 读数据
                                            client.read(buffer, buffer,
                                                    new CompletionHandler<Integer, ByteBuffer>() {
                                                        @Override
                                                        public void completed(Integer result,
                                                                              ByteBuffer attachment) {
                                                            buffer.flip();
                                                            System.out.println(new String(buffer.array()));
                                                            try {
                                                                client.close();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void failed(Throwable exc,
                                                                           ByteBuffer attachment) {

                                                        }
                                                    });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void failed(Throwable exc, Object attachment) {

                                    }
                                });
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {

                    }
                });
        // 主线程会马上结束，所以这里进行等待
        Thread.sleep(1000);
    }
}