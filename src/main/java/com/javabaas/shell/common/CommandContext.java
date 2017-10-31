package com.javabaas.shell.common;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBConfig;
import com.javabaas.shell.entity.JBServer;
import com.javabaas.shell.provider.PromptProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.javabaas.shell.util.Print.message;

/**
 * Created by Staryet on 15/8/21.
 * <p>
 * 上下文存储器
 */
@Component
public class CommandContext {

    @Autowired
    private PromptProvider promptProvider;
    @Autowired
    private ServerContext serverContext;
    private String currentClass;
    private JBApp currentApp;

    public boolean isServerAvailable() {
        if (serverContext.getCurrentServer() == null) {
            message("当前没有选择服务器 请使用'server'命令选择服务器");
            return false;
        }
        return true;
    }

    public boolean isAppAvailable() {
        if (!isServerAvailable()) {
            return false;
        }
        if (getCurrentApp() == null) {
            message("当前没有选择应用 请使用'use'命令选择应用");
            return false;
        }
        return true;
    }

    public boolean isClassAvailable() {
        if (!isAppAvailable()) {
            return false;
        }
        if (getCurrentClass() == null) {
            message("当前没有选择类 请使用'set'命令选择类");
            return false;
        }
        return true;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public JBApp getCurrentApp() {
        return currentApp;
    }

    public void setCurrentApp(JBApp currentApp) {
        this.currentApp = currentApp;
        this.currentClass = null;
        JBConfig.useApp(currentApp);
        changePrompt();
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
        changePrompt();
    }

    public JBServer getCurrentServer() {
        return serverContext.getCurrentServer();
    }

    public void setCurrentServer(int index) {
        if (serverContext.selectServer(index)) {
            this.currentApp = null;
            this.currentClass = null;
            changePrompt();
        }
    }

    private void changePrompt() {
        if (currentApp == null) {
            promptProvider.defaultPrompt();
        } else {
            if (currentClass == null) {
                promptProvider.setPrompt(currentApp.getName() + ">");
            } else {
                promptProvider.setPrompt(currentApp.getName() + " " + currentClass + ">");
            }
        }
    }

}
