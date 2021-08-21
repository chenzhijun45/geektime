package com.study.cthirdweek.gateway.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final String configFileName = "config.properties";
    private static volatile PropertiesUtil propertiesUtil = null;
    private final Properties properties = new Properties();

    private PropertiesUtil() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(configFileName);
            properties.load(is);
        } catch (IOException e) {
            log.error("加载配置文件异常 file={} e={}", configFileName, e.getMessage());
        }
    }

    private static PropertiesUtil getInstance() {
        if (propertiesUtil == null) {
            synchronized (PropertiesUtil.class) {
                if (propertiesUtil == null) {
                    propertiesUtil = new PropertiesUtil();
                }
            }
        }
        return propertiesUtil;
    }


    public static String get(String key) {
        String value = getInstance().properties.getProperty(key);
        if (value == null || value.trim().length() == 0) {
            log.error("配置信息不存在 key={}", key);
            value = "";
        }
        return value;
    }


    public static String get(String key, String defaultValue) {
        String value = getInstance().properties.getProperty(key);
        if (value == null || value.trim().length() == 0) {
            log.error("配置信息不存在 key={}", key);
            value = defaultValue;
        }
        return value;
    }

}
