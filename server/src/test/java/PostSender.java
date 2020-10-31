import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostSender {
    private static final Logger log = LoggerFactory.getLogger(PostSender.class);

    final String targetHost = "127.0.0.1";
    final String requestUrl = "/kill/kill/execute/locklock";
    final int portBegin = 21300;
    final int threadNum = 10;

    public void threadTest() {
        CountDownLatch cdLatch = new CountDownLatch(threadNum);
        Map<Integer, Double> responseTimeMap = new ConcurrentHashMap<>();

        long start = System.currentTimeMillis();
        for (int i = 1; i <= threadNum; i++) {
            Thread thread = new Thread(new MyRunnable(i, responseTimeMap, cdLatch));
            thread.start();
        }

        try {
            cdLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        long end = System.currentTimeMillis();

//        System.out.println("\nall response time:");
        double timesum = 0.0;
        for (Integer uid :
                responseTimeMap.keySet()) {
            double time = responseTimeMap.get(uid);
            timesum += time;
//            System.out.printf("%.0f\n", time);
        }

        Double avgTime = timesum / responseTimeMap.keySet().size();
        System.out.println("\ntotal time: " + (end - start));
        System.out.println("successful connection: " + responseTimeMap.size());
        System.out.printf("avg response time: %.0f\n", avgTime);
    }

    public static void main(String[] args) {
        PostSender ps = new PostSender();
        ps.threadTest();
    }


    class MyRunnable implements Runnable {
        private final int userId;
        private final Map<Integer, Double> timeMap;
        private final CountDownLatch latch;

        public MyRunnable(int userId, Map<Integer, Double> timeMap, CountDownLatch cdLatch) {
            this.userId = userId;
            this.timeMap = timeMap;
            this.latch = cdLatch;
        }

        @Override
        public void run() {
            socketPostRaw();
            latch.countDown();
        }

        public void socketPostRaw() {
            try (
                    Socket socket = new Socket(InetAddress.getLocalHost(), 8092);
//                    Socket socket = new Socket(InetAddress.getLocalHost(), 8092, InetAddress.getLocalHost(), portBegin + userId);
                 OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                 BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream()), StandardCharsets.UTF_8));
            ) {

                JSONObject obj = new JSONObject();
                obj.put("killId", "3");
                obj.put("userId", String.valueOf(userId));

                String data = obj.toJSONString();

                out.write("POST " + requestUrl + " HTTP/1.1\r\n");
                out.write("Host: " + targetHost + "\r\n");
                out.write("Content-Length: " + data.length() + "\r\n");
                out.write("Content-Type: application/json; charset=UTF-8\r\n");
//            out.write("Content-Type: application/x-www-form-urlencoded\r\n");
                out.write("\r\n");
                out.write(data);
                out.flush();
//                out.write("\r\n");
//                out.flush();
                socket.shutdownOutput();


                //开始计时
                long start = System.currentTimeMillis();

//            //打印响应
                String line = "";
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }

                long end = System.currentTimeMillis();
                long time = end - start;
                timeMap.put(userId, (double) time);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}

