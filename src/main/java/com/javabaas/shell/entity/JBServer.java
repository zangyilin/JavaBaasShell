package com.javabaas.shell.entity;

/**
 * Created by Codi on 2017/10/27.
 */
public class JBServer {

    private String host;
    private String key;

    public JBServer(String host, String key) {
        this.host = host;
        this.key = key;
    }

    public JBServer() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
