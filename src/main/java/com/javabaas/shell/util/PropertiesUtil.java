package com.javabaas.shell.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by Codi on 2017/10/27.
 */
public class PropertiesUtil {

    private static final String CONFIG_FILE_NAME = "/jbshell.properties";

    public static PropertiesStorage loadProperties() {
        try {
            FileInputStream inputStream = new FileInputStream(getPath() + CONFIG_FILE_NAME);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, PropertiesStorage.class);
        } catch (IOException exception) {
            return null;
        }
    }

    public static void saveProperties(PropertiesStorage properties) {
        File file = new File(getPath() + CONFIG_FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (filePath.endsWith(".jar") || filePath.endsWith(".exe")) {
            //可执行jar包运行的结果里包含".jar"
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        File file = new File(filePath);
        //得到windows下的正确路径
        filePath = file.getAbsolutePath();
        return filePath;
    }

}
