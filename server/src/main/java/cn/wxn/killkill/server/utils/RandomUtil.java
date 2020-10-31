package cn.wxn.killkill.server.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    public static String generateOrderCode(){
        //TODO:时间戳+N为随机数流水号
        return simpleDateFormat.format(DateTime.now().toDate()) + generateNum(4);
    }

    private static String generateNum(final int num){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < num; i++) {
            stringBuffer.append(threadLocalRandom.nextInt(9));
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
//        System.out.println(generateOrderCode());
        long maxId = -1L ^ (-1L << 5L);
        System.out.println(maxId);
        System.out.println(15323958142L & maxId);
        System.out.println(15323958142L % maxId);
    }
}
