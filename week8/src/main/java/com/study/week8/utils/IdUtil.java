package com.study.week8.utils;

import java.util.Random;

/**
 * ID生成工具类
 */
public class IdUtil {

    private static final String ORDER_PREFIX = "OS";


    /**
     * 生成订单id 固定前缀 + 雪花id
     */
    public static String generatorOrderId() {
        return ORDER_PREFIX + SnowflakeIdUtil.generateId();
    }

    /**
     * 生成userId 随机生成 [1, 3200]
     */
    public static long generatorUserId() {
        return new Random().nextInt(3200) + 1;
    }

}
