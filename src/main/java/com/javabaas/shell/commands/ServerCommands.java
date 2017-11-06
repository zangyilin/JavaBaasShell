package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.common.ServerContext;
import com.javabaas.shell.entity.JBServer;
import com.javabaas.shell.util.PromptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static com.javabaas.shell.util.Print.message;
import static com.javabaas.shell.util.Print.success;

/**
 * Created by Codi on 2017/10/26.
 */
@Component
public class ServerCommands implements CommandMarker {

    @Autowired
    private CommandContext commandContext;
    @Autowired
    private ServerContext serverContext;

    @CliCommand(value = "servers", help = "显示服务器列表")
    public void list() {
        List<JBServer> servers = serverContext.getServers();
        List<String> values = new LinkedList<>();
        if (servers != null && servers.size() > 0) {
            for (JBServer server : servers) {
                values.add(server.getHost());
            }
            int index = PromptUtil.choose(values, "请选择服务器:", 0);
            if (index != 0) {
                commandContext.setCurrentServer(index - 1);
            }
        } else {
            message("当前没有配置服务器 添加服务器");
            add();
        }
    }

    @CliCommand(value = "server add", help = "增加服务器")
    public void add() {
        String host = PromptUtil.string("请输入服务器地址:", "http://127.0.0.1:8080/api");
        if (JBUtils.isEmpty(host)) {
            return;
        }
        String key = PromptUtil.string("请输入AdminKey:", "JavaBaas");
        if (JBUtils.isEmpty(key)) {
            return;
        }
        JBServer server = new JBServer(host, key);
        serverContext.addServer(server);
        success("服务器添加成功");
    }

    @CliCommand(value = "server del", help = "删除服务器")
    public void remove() {
        List<JBServer> servers = serverContext.getServers();
        List<String> values = new LinkedList<>();
        if (servers != null && servers.size() > 0) {
            for (JBServer server : servers) {
                values.add(server.getHost());
            }
            int index = PromptUtil.choose(values, "请选择要删除的服务器:", 0);
            if (index != 0) {
                serverContext.removeServer(index - 1);
            }
        } else {
            message("当前没有配置服务器 请使用'server add'命令添加服务器");
        }
    }

}
