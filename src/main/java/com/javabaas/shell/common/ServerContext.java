package com.javabaas.shell.common;

import com.javabaas.javasdk.JB;
import com.javabaas.shell.entity.JBServer;
import com.javabaas.shell.util.Print;
import com.javabaas.shell.util.PropertiesStorage;
import com.javabaas.shell.util.PropertiesUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Codi on 2017/10/27.
 */
@Service
public class ServerContext {

    private JBServer currentServer;

    public void loadServices() {
        PropertiesStorage properties = PropertiesUtil.loadProperties();
        if (properties != null) {
            Integer index = properties.getServerIndex();
            List<JBServer> servers = properties.getServers();
            if (index != null && servers != null && servers.size() > index) {
                selectServer(index);
            }
        } else {
            JBServer server = new JBServer("http://localhost:8080/api", "JavaBaas");
            addServer(server);
        }
    }

    public List<JBServer> getServers() {
        PropertiesStorage properties = PropertiesUtil.loadProperties();
        return properties == null ? null : properties.getServers();
    }

    public JBServer getCurrentServer() {
        return currentServer;
    }

    public void addServer(JBServer server) {
        PropertiesStorage properties = PropertiesUtil.loadProperties();
        if (properties == null) {
            properties = new PropertiesStorage();
        }
        if (properties.getServers() == null) {
            properties.setServers(new LinkedList<>());
        }
        properties.getServers().add(server);
        PropertiesUtil.saveProperties(properties);
        //修改当前服务器
        selectServer(properties.getServers().size() - 1);
    }

    public void removeServer(int index) {
        PropertiesStorage properties = PropertiesUtil.loadProperties();
        if (properties == null) {
            return;
        }
        if (properties.getServers() == null) {
            return;
        }
        if (properties.getServers().size() < index) {
            return;
        }
        properties.getServers().remove(index);
        properties.setServerIndex(null);
        currentServer = null;
        PropertiesUtil.saveProperties(properties);
        Print.message("服务器删除成功");
    }

    public boolean selectServer(int index) {
        PropertiesStorage properties = PropertiesUtil.loadProperties();
        if (properties != null) {
            List<JBServer> servers = properties.getServers();
            if (servers != null && servers.size() >= index) {
                currentServer = servers.get(index);
                properties.setServerIndex(index);
                PropertiesUtil.saveProperties(properties);
                Print.message("当前服务器地址切换为 " + currentServer.getHost());
                JB.initAdmin(currentServer.getHost(), currentServer.getKey());
                return true;
            }
        }
        return false;
    }

}
