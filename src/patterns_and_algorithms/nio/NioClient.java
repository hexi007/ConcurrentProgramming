package patterns_and_algorithms.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * description NIO 客户端
 * create 2020-11-10 19:00
 *
 * @author 27771
 **/
public class NioClient {

    private Selector selector;

    String ip;
    int port;

    public NioClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void init () throws IOException {
        // 获得服务端的 SocketChannel 实例
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = SelectorProvider.provider().openSelector();
        // connect() 方法返回时，连接不一定成功，还需要使用 finishConnect() 再次确认
        channel.connect(new InetSocketAddress(ip, port));
        // 注册为连接
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    private void working() throws IOException {
        while (true) {
            if (!selector.isOpen()) {
                break;
            }
            // 没有任何事件准备就绪就会阻塞
            selector.select();
            Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();

                if (selectionKey.isConnectable()) {
                    connect(selectionKey);
                } else if (selectionKey.isReadable()) {
                    read(selectionKey);
                }
            }
        }
    }

    /**
     * 连接
     */
    private void connect(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        // 如果正在连接，则完成连接
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }

        channel.configureBlocking(false);
        // 向 Channel 写入数据
        channel.write(ByteBuffer.wrap(new String("hello server!\r\n").getBytes()));
        // 注册读事件
        channel.register(this.selector, SelectionKey.OP_READ);
    }

    /**
     * 读服务器发来数据
     */
    private void read(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        // 创建读取的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(100);
        // 从 Channel 读取数据
        channel.read(buffer);
        String msg = new String(buffer.array()).trim();
        System.out.println(" receive : " + msg);
        channel.close();
        selectionKey.selector().close();
    }

    public static void main(String[] args) throws IOException {
        NioClient nioClient = new NioClient("127.0.0.1", 8000);
        nioClient.init();
        nioClient.working();
    }
}