package com.javabaas.shell.util;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Created by Codi on 16/7/21.
 */
public class PropertiesUtil {

    private static final String DEFAULT_HOST = "http://127.0.0.1:8080/api/";
    private static final String DEFAULT_KEY = "JavaBaas";
    private static String host;
    private static String key;

    private PropertiesUtil() {
    }

    static {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            //首先从同目录下加载
            inputStream = new FileInputStream(getPath() + "/config.properties");
        } catch (FileNotFoundException ignored) {
        }
        if (inputStream == null) {
            //文件加载失败 从资源文件处加载
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        }
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            key = properties.getProperty("key");
            host = properties.getProperty("host");
        }
    }

    public static String getHost() {
        return host != null ? host : DEFAULT_HOST;
    }

    public static String getKey() {
        return key != null ? key : DEFAULT_KEY;
    }

    private static String getPath() {
        URL url = PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            //转化为utf-8编码
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            //可执行jar包运行的结果里包含".jar"
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        File file = new File(filePath);
        //得到windows下的正确路径
        filePath = file.getAbsolutePath();
        return filePath;
    }
}
