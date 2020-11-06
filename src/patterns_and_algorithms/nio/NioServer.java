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
 * create 2020-11-06 20:25
 *
 * @author 27771
 **/
public class NioServer {
    private static Selector selector;
    private static final ExecutorService TP = Executors.newCachedThreadPool();
    private static final Map<Socket, Long> TIME_STAT = new HashMap<>(10240);

    private void startServer() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        InetSocketAddress isa = new InetSocketAddress("localhost", 8000);
        ssc.socket().bind(isa);

        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> readKey = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readKey.iterator();
            long e;
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();

                if(selectionKey.isAcceptable()) {
                    doAccept(selectionKey);
                } else if (selectionKey.isValid() && selectionKey.isReadable()) {
                    if(!TIME_STAT.containsKey(((SocketChannel)selectionKey.channel()).socket())){
                        TIME_STAT.put(((SocketChannel)selectionKey.channel()).socket(),
                                System.currentTimeMillis());
                        doRead(selectionKey);
                    }
                } else if (selectionKey.isValid() && selectionKey.isWritable()) {
                    doWrite(selectionKey);
                    e = System.currentTimeMillis();
                    long b = TIME_STAT.remove(((SocketChannel)selectionKey.channel()).socket());
                    System.out.println("spend : " + (e - b) + " ms");
                }
            }
        }
    }

    private class EchoClient {
        private final LinkedList<ByteBuffer> outputQueue = new LinkedList<>();

        public LinkedList<ByteBuffer> getOutputQueue() {
            return outputQueue;
        }

        public void enqueue(ByteBuffer bb) {
            outputQueue.addFirst(bb);
        }
    }

    private void doAccept(SelectionKey selectionKey) {

        try {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            SocketChannel clientChannel = server.accept();
            clientChannel.configureBlocking(false);

            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
            EchoClient echoClient = new EchoClient();
            clientKey.attach(echoClient);

            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from " + clientAddress.getHostAddress() + " ...");

        } catch (IOException e) {
            System.out.println("Failed to accept new client...");
            e.printStackTrace();
        }
    }

    private void doRead(SelectionKey selectionKey) {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
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

        bb.flip();
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

    private class HandleMsg implements Runnable {

        SelectionKey selectionKey;
        ByteBuffer bb;

        public HandleMsg(SelectionKey selectionKey, ByteBuffer bb) {
            this.selectionKey = selectionKey;
            this.bb = bb;
        }

        @Override
        public void run() {
            EchoClient echoClient = (EchoClient) selectionKey.attachment();
            echoClient.enqueue(bb);
            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            selector.wakeup();
        }
    }

    private void doWrite(SelectionKey selectionKey) {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        EchoClient echoClient = (EchoClient) selectionKey.attachment();
        LinkedList<ByteBuffer> outputQueue = echoClient.getOutputQueue();

        ByteBuffer bb = outputQueue.getLast();
        try {
            int len = channel.write(bb);
            if (len == -1) {
                disconnect(selectionKey);
                return;
            }

            if (bb.remaining() == 0) {
                outputQueue.removeLast();
            }
        } catch (IOException e) {
            System.out.println("Failed to read from client");
            e.printStackTrace();
            disconnect(selectionKey);
        }

        if (outputQueue.size() == 0) {
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.startServer();
    }
}