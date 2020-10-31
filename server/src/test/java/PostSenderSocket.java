import net.minidev.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PostSenderSocket {

    private HttpURLConnection createConnection(String ADD_URL){
        try {
            URL url = new URL(ADD_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            //connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            connection.connect();
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendPost(int killId, int userId, List<Double> timeList, HttpURLConnection connection){
        try {
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject obj = new JSONObject();

            obj.put("killId", "3");
            obj.put("userId", String.valueOf(userId));
            out.write(obj.toString().getBytes("UTF-8"));//这样可以处理中文乱码问题
            out.flush();
            out.close();


            //读取响应
            long start = System.currentTimeMillis();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            long end = System.currentTimeMillis();
            long time = end - start;
            timeList.add((double) time);


            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void execute(String postURL){
//        HttpURLConnection conn = createConnection(postURL);
//
//        int threadNum = 1000;
//        CountDownLatch cdLatch = new CountDownLatch(threadNum);
//        List<Double> responseTimeList = new LinkedList<>();
//
//        long start = System.currentTimeMillis();
//        for (int i = 1; i <= threadNum; i++) {
//            Thread thread = new Thread(new MyRunnable(i, responseTimeList, cdLatch));
//            thread.start();
//        }
//        long end = System.currentTimeMillis();
//
//        try {
//            cdLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("\nall response time:");
//        Double timesum = 0.0;
//        for (Double t :
//                responseTimeList) {
//            timesum += t;
//            System.out.printf("%.0f\n", t);
//        }
//        Double avgTime = timesum / responseTimeList.size();
//        System.out.println("\ntotal time: " + (end - start));
//        System.out.printf("avg response time: %.0f\n", avgTime);
//    }
//
//    public static void main(String[] args) {
//        PostSenderSocket pss = new PostSenderSocket();
//        pss.execute("http://127.0.0.1:8092/kill/kill/execute/locklock");
//    }
}
