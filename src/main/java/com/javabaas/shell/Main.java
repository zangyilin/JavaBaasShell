package com.javabaas.shell;

import com.javabaas.javasdk.JBConfig;
import com.javabaas.shell.util.PropertiesUtil;
import org.springframework.shell.Bootstrap;

import java.io.IOException;


/**
 * Created by Staryet on 15/8/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        JBConfig.initAdmin(PropertiesUtil.getHost(), PropertiesUtil.getKey());
        Bootstrap.main(args);
    }

}