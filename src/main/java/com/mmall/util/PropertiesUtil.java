package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by guolin
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    // Properties以字符串形式存储键值对
    private static Properties props;

    // 在Tomcat启动的时候就要读取到这些配置，所以采用静态块解决这个问题（执行顺序，静态代码块>普通代码块>构造代码块）
    // 在类加载的时候先执行，并只执行一次
    static {
        // 配置文件名
        String fileName = "mmall.properties";

        props = new Properties();
        try {
            /*
            Properties的load方法
            Properties的load方法其实就是传进去一个输入流，字节流或者字符流，
            字节流利用InputStreamReader转化为字符流，然后字符流用BufferedReader包装，
            BufferedReader读取properties配置文件，每次读取一行，分割成两个字符串。
            因为Properties是Map的子类，然后用put将两个字符串装进Properties对象
            */

            // getClassLoader()取得该Class对象的类装载器
            // getResourceAsStream()返回一个InputStream对象
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }

    /**
     * 通过key提取value
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        // trim()去除两边的空格
        String value = props.getProperty(key.trim());

        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    /**
     * 通过key提取value，如果value为空，则传回defaultValue
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }
}
