package com.javabaas.shell.common;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBConfig;
import com.javabaas.shell.provider.PromptProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Staryet on 15/8/21.
 * <p>
 * 上下文存储器
 */
@Component
public class CommandContext {

    @Autowired
    private PromptProvider promptProvider;
    private String currentClass;
    private JBApp currentApp;

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
