package com.javabaas.shell.provider;

import com.javabaas.shell.common.ServerContext;
import com.javabaas.shell.entity.JBServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider {

    @Autowired
    private ServerContext serverContext;

    public String getBanner() {
        serverContext.loadServices();
        JBServer currentServer = serverContext.getCurrentServer();
        String banner =
                "   ___                     ______                    " + OsUtils.LINE_SEPARATOR +
                        "  |_  |                    | ___ \\                   " + OsUtils.LINE_SEPARATOR +
                        "    | |  __ _ __   __ __ _ | |_/ /  __ _   __ _  ___ " + OsUtils.LINE_SEPARATOR +
                        "    | | / _` |\\ \\ / // _` || ___ \\ / _` | / _` |/ __|" + OsUtils.LINE_SEPARATOR +
                        "/\\__/ /| (_| | \\ V /| (_| || |_/ /| (_| || (_| |\\__ \\" + OsUtils.LINE_SEPARATOR +
                        "\\____/  \\__,_|  \\_/  \\__,_|\\____/  \\__,_| \\__,_||___/" + OsUtils.LINE_SEPARATOR +
                        "Version:" + this.getVersion();
        if (currentServer == null) {
            banner += OsUtils.LINE_SEPARATOR + "当前没有配置服务器 请使用'server'命令选择服务器";
        } else {
            banner += OsUtils.LINE_SEPARATOR + "Host:" +
                    currentServer.getHost() + OsUtils.LINE_SEPARATOR +
                    "AdminKey:" + currentServer.getKey();
        }
        return banner;
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String getWelcomeMessage() {
        return null;
    }

    @Override
    public String getProviderName() {
        return "JavaBaas";
    }

}