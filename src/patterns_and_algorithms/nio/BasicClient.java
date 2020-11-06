package patterns_and_algorithms.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * description 普通客户端
 * create 2020-11-06 17:02
 *
 * @author 27771
 **/
public class BasicClient {

    private Socket client;
    PrintWriter writer = null;
    BufferedReader reader = null;

    public void client() {
        try {
            client = new Socket();
            // 设置 Socket
            client.connect(new InetSocketAddress("localhost", 8000));
            writer = new PrintWriter(client.getOutputStream(), true);
            writer.println("Hello \nthis is client");
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String outputLine;
            while ((outputLine = reader.readLine()) != null) {
                System.out.println("from server : " + outputLine);
            }
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

    public static void main(String[] args) {
        BasicClient basicClient = new BasicClient();
        basicClient.client();
    }
}