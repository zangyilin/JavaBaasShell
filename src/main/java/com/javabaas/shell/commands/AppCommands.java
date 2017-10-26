package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PromptUtil;
import com.javabaas.shell.util.SignUtil;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Codi on 15/9/22.
 * <p>
 * 应用命令
 */
@Component
public class AppCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private SignUtil signUtil;

    @CliAvailabilityIndicator({"export", "info", "account"})
    public boolean isAvailable() {
        return context.getCurrentApp() != null;
    }

    @CliCommand(value = "apps", help = "Show app list.")
    public void list() {
        try {
            List<JBApp> list = JBApp.list();
            list.forEach(o -> System.out.println(o.getName()));
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "app add", help = "Add app.")
    public void add(@CliOption(key = {""}, mandatory = true) final String name) {
        try {
            JBApp app = new JBApp();
            app.setName(name);
            app.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("应用创建成功.").reset());
            set(name);
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "app del", help = "Delete class.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
        //显示类信息
        List<JBApp> list = JBApp.list();
        final boolean[] flag = {false};
        list.forEach(app -> {
            if (app.getName().equals(name)) {
                flag[0] = true;
                try {
                    if (PromptUtil.check("确认要删除应用?")) {
                        app.delete();
                        context.setCurrentApp(null);
                        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("删除成功").reset());
                    }
                } catch (HttpClientErrorException e) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
                }
            }
        });
        //未找到应用
        if (!flag[0]) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("未找到应用!").reset());
        }
    }

    @CliCommand(value = "use", help = "Set current app.")
    public void set(@CliOption(key = {""}, help = "app name") final String name) {
        if (name == null) {
            //重置当前应用
            context.setCurrentApp(null);
        } else {
            try {
                List<JBApp> list = JBApp.list();
                final boolean[] flag = {false};
                list.forEach(app -> {
                    if (app.getName().equals(name)) {
                        JBApp jbApp = JBApp.get(app.getId());
                        flag[0] = true;
                        System.out.println("Set current app to " + Ansi.ansi().fg(Ansi.Color.GREEN).a(jbApp.getName()).reset());
                        context.setCurrentApp(jbApp);
                        return;
                    }
                });
                //未找到应用
                if (!flag[0]) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("未找到应用!").reset());
                }
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "export", help = "Export tha app.")
    public void export() {
        JBApp app = context.getCurrentApp();
        JBApp.JBAppExport appExport = JBApp.export(app.getId());
        System.out.println(JBUtils.writeValueAsString(appExport));
    }

    @CliCommand(value = "info", help = "Get app info")
    public void appInfo() {
        JBApp app = context.getCurrentApp();
        JBApp jbApp = JBApp.get(app.getId());
        System.out.println(jbApp);
    }

    @CliCommand(value = "import", help = "Import tha app.")
    public void importData(@CliOption(key = {""}, mandatory = true, help = "app name") final String app) {
        try {
            JBApp.importData(app);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("导入成功").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "token", help = "Get Token.")
    public void token() {
        //获取令牌
        String timestamp = signUtil.getTimestamp();
        String nonce = UUID.randomUUID().toString().replace("-", "");
        System.out.println("Timestamp:  " + timestamp);
        System.out.println("Nonce:  " + nonce);
        System.out.println("AdminSign:  " + signUtil.getAdminSign(timestamp, nonce));
        if (context.getCurrentApp() != null) {
            System.out.println("AppId:  " + signUtil.getAppId());
            System.out.println("Key:  " + context.getCurrentApp().getKey());
            System.out.println("MasterKey:  " + context.getCurrentApp().getMasterKey());
            System.out.println("Sign:  " + signUtil.getSign(timestamp, nonce));
            System.out.println("MasterSign:  " + signUtil.getMasterSign(timestamp, nonce));
        }
    }

    @CliCommand(value = "account", help = "Set Account.")
    public void setAccount() throws JsonProcessingException {
        try {
            List<String> accountTypes = getAccountTypes();
            int accountType = PromptUtil.choose(accountTypes, "请选择AccountType", 0);
            if (accountType == 0) {
                return;
            }
            String key = PromptUtil.string("请输入key值");
            if (JBUtils.isEmpty(key)) {
                return;
            }
            String secret = PromptUtil.string("请输入secret值");
            if (JBUtils.isEmpty(secret)) {
                return;
            }

            JBApp.Account account = new JBApp.Account();
            account.setKey(key);
            account.setSecret(secret);

            JBApp.setAccount(JBApp.AccountType.getType(accountType), account);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("更新成功").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    private List<String> getAccountTypes() {
        List<String> list = new ArrayList<>();
        for (JBApp.AccountType type : JBApp.AccountType.values()) {
            list.add(type.getValue());
        }
        return list;
    }

}
