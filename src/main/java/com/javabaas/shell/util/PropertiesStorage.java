package com.javabaas.shell.util;

import com.javabaas.shell.entity.JBServer;

import java.util.List;

/**
 * Created by Codi on 2017/10/27.
 */
public class PropertiesStorage {

    private List<JBServer> servers;
    private Integer serverIndex;

    public List<JBServer> getServers() {
        return servers;
    }

    public void setServers(List<JBServer> servers) {
        this.servers = servers;
    }

    public Integer getServerIndex() {
        return serverIndex;
    }

    public void setServerIndex(Integer serverIndex) {
        this.serverIndex = serverIndex;
    }
}
