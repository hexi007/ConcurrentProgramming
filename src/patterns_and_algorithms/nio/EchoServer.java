package patterns_and_algorithms.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * description Echo 服务器 <br/>
 * 服务器会为每一个客户端连接启用一个线程，这个新的线程专门为这个客户端服务 <br/>
 * 同时为了接受客户端的连接，服务器额外使用一个派发线程 <br/>
 * 在相同支持的线程范围内尽可能多的支持客户端数量 <br/>
 * 同单线程服务器相比可以更好的使用多核 CPU <br/>
 * create 2020-11-06 16:36
 *
 * @author 27771
 **/
public class EchoServer {

    private static final ExecutorService TP = Executors.newCachedThreadPool();

    /**
     *  读取 clientSocket 并将其内容返回
     */
    private static class HandleMsg implements Runnable {

        Socket clientSocket;

        public HandleMsg(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            BufferedReader bR = null;
            PrintWriter pW = null;
            try {
                bR = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pW = new PrintWriter(clientSocket.getOutputStream(), true);
                // 从 InputStreamReader 中读取客户端发送的数据
                String inputLine;
                long start = System.currentTimeMillis();
                while ((inputLine = bR.readLine()) != null) {
                    pW.println(inputLine);
                }
                long end = System.currentTimeMillis();
                System.out.println("spend : " + (end - start) + " ms");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bR != null){
                        bR.close();
                    }
                    if(pW != null){
                        pW.close();
                    }
                    //任务完成，关闭 clientSocket
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket echoServer = null;
        Socket clientSocket;

        try {
            // echoServer 在 8000 端口等待
            echoServer = new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                clientSocket = echoServer.accept();
                System.out.println(clientSocket.getRemoteSocketAddress() + " connect...");
                TP.execute(new HandleMsg(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}