package patterns_and_algorithms.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description NIO 网络编程
 * Channel  通道 可以和文件或者网络 socket 对应
 * Buffer   数据需要包装成 Buffer 才能和 Channel 交互
 * Selector 选择器 一个 Selector 可以管理多个 Channel 的实现（SelectableChannel）
 *          当 SelectableChannel 数据准备好，Selector 就会接到通知，得到已经准备好的数据
 * create 2020-11-06 20:25
 *
 * @author 27771
 **/
public class NioServer {
    /**
     * Selector 管理所有网络连接
     */
    private static Selector selector;
    /**
     * ExecutorService 线程池用于对每一个客户端进行处理
     *                 每一个请求都会委托给线程池中的线程进行实际的处理
     */
    private static final ExecutorService TP = Executors.newCachedThreadPool();
    /**
     * Map<Socket, Long> 统计服务器线程在一个客户端上花费的时间
     */
    private static final Map<Socket, Long> TIME_STAT = new HashMap<>(10240);

    private void startServer() throws IOException {
        // 通过工厂方法获得一个实例
        selector = SelectorProvider.provider().openSelector();
        // 获得服务端的 SocketChannel 实例
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 设置为非阻塞模式，这种模式下可以向 Channel 注册感兴趣的事件
        // 在数据准备好时得到必要的通知
        ssc.configureBlocking(false);

        // 端口绑定
        InetSocketAddress isa = new InetSocketAddress("localhost", 8000);
        // 获取与此通道关联的服务器套接字。将 ServerSocket 绑定到特定地址（IP 地址和端口号）
        ssc.socket().bind(isa);

        // 将 ServerSocketChannel 绑定到 Selector 上，并注册它感兴趣的时间为 Accept
        // SelectionKey 表示一对 Selector 和 Channel 的关系
        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        // 无穷循环，主要任务就是等待-分发网络消息
        while (true) {
            // select() 是一个阻塞方法，如果当前没有任何数据准备好就会等待。
            // 一但数据可读，就返回就绪的 SelectionKey 的数量
            selector.select();
            // 获取准备好的 SelectionKey，遍历这个集合处理所有的 Channel 数据
            Set<SelectionKey> readKey = selector.selectedKeys();
            // 集合迭代器
            Iterator<SelectionKey> iterator = readKey.iterator();
            long e;
            while (iterator.hasNext()) {
                // 根据迭代器获取一个 SelectionKey 实例
                SelectionKey selectionKey = iterator.next();
                // 移除将要处理的 SelectionKey，否则会重复处理相同的 SelectionKey
                iterator.remove();

                // 检查当前 SelectionKey 所代表的 Channel 是否在 Acceptable 状态
                // 如果是，就进行客户端的接收
                if(selectionKey.isAcceptable()) {
                    doAccept(selectionKey);
                }
                // Channel 是否可读
                else if (selectionKey.isValid() && selectionKey.isReadable()) {
                    // 统计系统处理每一个连接的时间
                    if(!TIME_STAT.containsKey(((SocketChannel)selectionKey.channel()).socket())){
                        TIME_STAT.put(((SocketChannel)selectionKey.channel()).socket(),
                                System.currentTimeMillis());
                        doRead(selectionKey);
                    }
                }
                // Channel 是否可读
                else if (selectionKey.isValid() && selectionKey.isWritable()) {
                    doWrite(selectionKey);
                    e = System.currentTimeMillis();
                    long b = TIME_STAT.remove(((SocketChannel)selectionKey.channel()).socket());
                    // 输出处理时延
                    System.out.println("spend : " + (e - b) + " ms");
                }
            }
        }
    }

    private static class EchoClient {
        /*
         * outputQueue 保存在回复给这个客户端的所有信息
         */
        private final LinkedList<ByteBuffer> outputQueue = new LinkedList<>();

        public LinkedList<ByteBuffer> getOutputQueue() {
            return outputQueue;
        }

        public void enqueue(ByteBuffer bb) {
            outputQueue.addFirst(bb);
        }
    }

    /**
     *  与客户端建立连接
     */
    private void doAccept(SelectionKey selectionKey) {

        try {
            // 当有一个新的客户端连接时，就会有一个新的 Channel 产生代表这个连接
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            // 生成的 clientChannel 就表示和客户端通信的通道
            SocketChannel clientChannel = server.accept();
            // 将这个 Channel 配置为非阻塞模式
            clientChannel.configureBlocking(false);

            // 将新生成的 Channel 注册到 selector 选择器，并告诉 Selector 只对 OP_READ 操作感兴趣
            // 当 Selector 发现这个 Channel 已经准备好读时，就能给线程一个通知
            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
            // 一个 EchoClient 实例代表一个客户端
            EchoClient echoClient = new EchoClient();
            // 将这个客户端实例作为附件，附加到表示这个连接的 SelectionKey 上
            clientKey.attach(echoClient);

            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from " + clientAddress.getHostAddress() + " ...");

        } catch (IOException e) {
            System.out.println("Failed to accept new client...");
            e.printStackTrace();
        }
    }

    /**
     * 当 Channel 可以读取时，doRead() 就会被调用
     */
    private void doRead(SelectionKey selectionKey) {
        // 通过 selectionKey 获取当前客户端的 Channel
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        // 8 k 的缓冲区
        ByteBuffer bb = ByteBuffer.allocate(8192);
        int len;

        try {
            len = channel.read(bb);
            if(len < 0) {
                disconnect(selectionKey);
                return;
            }
        } catch (IOException e) {
            System.out.println("Failed to read from client");
            e.printStackTrace();
            disconnect(selectionKey);
            return;
        }

        // 重置缓冲区
        bb.flip();
        // 使用线程池进行数据处理
        TP.execute(new HandleMsg(selectionKey, bb));
    }

    private void disconnect(SelectionKey selectionKey) {
        try {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            channel.close();
            selectionKey.selector().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class HandleMsg implements Runnable {

        SelectionKey selectionKey;
        ByteBuffer bb;

        public HandleMsg(SelectionKey selectionKey, ByteBuffer bb) {
            this.selectionKey = selectionKey;
            this.bb = bb;
        }

        @Override
        public void run() {
            EchoClient echoClient = (EchoClient) selectionKey.attachment();
            // 将接收到的数据 压入 EchoClient 队列
            echoClient.enqueue(bb);
            // 将写操作（OP_WRITE） 也作为感兴趣的事件进行提交
            // 在通道准备好写入时，就能通知写线程
            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            selector.wakeup();
        }
    }

    /**
     * 执行写操作
     */
    private void doWrite(SelectionKey selectionKey) {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        // 通过 selectionKey 获取当前客户端的 Channel
        EchoClient echoClient = (EchoClient) selectionKey.attachment();
        LinkedList<ByteBuffer> outputQueue = echoClient.getOutputQueue();

        // 获得列表顶部元素
        ByteBuffer bb = outputQueue.getLast();
        try {
            // 进行写回操作
            int len = channel.write(bb);
            if (len == -1) {
                disconnect(selectionKey);
                return;
            }

            if (bb.remaining() == 0) {
                // 全部发送完毕，移除这个缓存对象
                outputQueue.removeLast();
            }
        } catch (IOException e) {
            System.out.println("Failed to read from client");
            e.printStackTrace();
            disconnect(selectionKey);
        }

        if (outputQueue.size() == 0) {
            // 将写事件移除
            // 如果不移除，每次 Channel 准备好写时都会执行 doWrite() 方法
            // 而实际上无数据可写，这是不合理的
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        // 处理 HeavySocketClient 平均延迟 1 ms
        nioServer.startServer();
    }
}