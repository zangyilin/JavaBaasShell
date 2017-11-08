package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBConfig;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PromptUtil;
import com.javabaas.shell.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.javabaas.shell.util.Print.*;

/**
 * Created by Codi on 15/9/22.
 * 应用命令
 */
@Component
public class AppCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private SignUtil signUtil;

    @CliCommand(value = "export", help = "导出应用结构")
    public void export() {
        if (context.isAppAvailable()) {
            try {
                JBApp app = context.getCurrentApp();
                JBApp.JBAppExport appExport = JBApp.export(app.getId());
                message(JBUtils.writeValueAsString(appExport));
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "info", help = "获取应用信息")
    public void appInfo() {
        if (context.isAppAvailable()) {
            try {
                JBApp app = context.getCurrentApp();
                JBApp jbApp = JBApp.get(app.getId());
                message(jbApp);
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "import", help = "导入应用结构")
    public void importData(@CliOption(key = {""}, mandatory = true, help = "app name") final String app) {
        try {
            JBApp.importData(app);
            success("导入成功");
        } catch (JBException e) {
            error(e.getMessage());
        }
    }

    @CliCommand(value = "token", help = "获取令牌")
    public void token() {
        if (context.isAppAvailable()) {
            //获取令牌
            String timestamp = signUtil.getTimestamp();
            String nonce = UUID.randomUUID().toString().replace("-", "");
            message("Timestamp:  " + timestamp);
            message("Nonce:  " + nonce);
            message("AdminSign:  " + signUtil.getAdminSign(timestamp, nonce));
            if (context.getCurrentApp() != null) {
                message("AppId:  " + signUtil.getAppId());
                message("Key:  " + context.getCurrentApp().getKey());
                message("MasterKey:  " + context.getCurrentApp().getMasterKey());
                message("Sign:  " + signUtil.getSign(timestamp, nonce));
                message("MasterSign:  " + signUtil.getMasterSign(timestamp, nonce));
            }
        }
    }




}
