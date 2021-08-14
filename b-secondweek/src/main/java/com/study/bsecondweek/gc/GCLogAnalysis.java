package com.study.bsecondweek.gc;

import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

public class GCLogAnalysis {

    private static Random random = new Random();

    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + 1000L;
        int cacheGarbageSize = 2000;
        Object[] cacheGarbage = new Object[cacheGarbageSize];
        LongAdder count = new LongAdder();
        System.out.println("开始执行...");
        while (System.currentTimeMillis() < endTime) {
            Object garbage = generateGarbage(102400);
            count.increment();
            int randomIndex = random.nextInt(2 * cacheGarbageSize);
            if (randomIndex < cacheGarbageSize) {
                cacheGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束：累计生产对象" + count + "次");

//        List<Object> list = new ArrayList<>();
//        while (System.currentTimeMillis() < endTime) {
//            list.add(new Object());
//
//        }


    }


    private static Object generateGarbage(int max) {
        int randomInt = random.nextInt(max);
        int type = max % 4;
        Object result;
        switch (type) {
            case 0:
                result = new int[randomInt];
                break;
            case 1:
                result = new byte[randomInt];
                break;
            case 2:
                result = new double[randomInt];
                break;
            default:
                StringBuilder sb = new StringBuilder();
                String str = "randomStringAnyThong";
                while (sb.length() < randomInt) {
                    sb.append(str).append(max).append(randomInt);
                }
                result = sb.toString();
                break;
        }
        return result;
    }

}
