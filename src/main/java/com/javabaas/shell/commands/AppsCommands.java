package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PromptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.javabaas.shell.util.Print.*;

/**
 * Created by Codi on 2017/10/30.
 */
@Component
public class AppsCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliCommand(value = "apps", help = "显示应用列表")
    public void list() {
        if (context.isServerAvailable()) {
            try {
                List<JBApp> list = JBApp.list();
                list.forEach(o -> message(o.getName()));
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "app add", help = "添加应用")
    public void add(@CliOption(key = {""}, mandatory = true) final String name) {
        if (context.isServerAvailable()) {
            try {
                JBApp app = new JBApp();
                app.setName(name);
                app.save();
                success("应用创建成功");
                use(name);
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "app del", help = "删除应用")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
        if (context.isServerAvailable()) {
            //显示类信息
            try {
                List<JBApp> list = JBApp.list();
                final boolean[] flag = {false};
                list.forEach(app -> {
                    if (app.getName().equals(name)) {
                        flag[0] = true;
                        try {
                            if (PromptUtil.check("确认要删除应用?")) {
                                app.delete();
                                context.setCurrentApp(null);
                                success("删除成功");
                            }
                        } catch (JBException e) {
                            error(e.getMessage());
                        }
                    }
                });
                //未找到应用
                if (!flag[0]) {
                    error("未找到应用!");
                }
            } catch (JBException e) {
                error(e.getMessage());
            }

        }
    }

    @CliCommand(value = "use", help = "设置当前应用")
    public void use(@CliOption(key = {""}, help = "app name") final String name) {
        if (context.isServerAvailable()) {
            try {
                if (name == null) {
                    List<JBApp> list = JBApp.list();
                    List<String> values = new LinkedList<>();
                    for (JBApp app : list) {
                        values.add(app.getName());
                    }
                    int index = PromptUtil.choose(values, "请选择应用:", 0);
                    if (index == 0) {
                        //重置当前应用
                        context.setCurrentApp(null);
                    } else {
                        JBApp app = list.get(index - 1);
                        context.setCurrentApp(app);
                        message("设置当前应用为 " + app.getName());
                    }
                } else {
                    List<JBApp> list = JBApp.list();
                    boolean flag = false;
                    for (JBApp app : list) {
                        if (app.getName().equals(name)) {
                            flag = true;
                            context.setCurrentApp(app);
                            message("设置当前应用为 " + app.getName());
                            break;
                        }
                    }
                    //未找到应用
                    if (!flag) {
                        error("未找到应用!");
                    }
                }
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "config", help = "设置appConfig")
    public void setConfig() {
        if (context.isAppAvailable()) {
            try {
                List<String> configTypes = getConfigTypes();
                int configType = PromptUtil.choose(configTypes, "请选择所要设置的Config", 0);
                if (configType == 0) {
                    return;
                }
                String value = PromptUtil.string("请输入值");
                if (JBUtils.isEmpty(value)) {
                    return;
                }
                JBApp.JBAppConfig config = new JBApp.JBAppConfig();
                config.setAppConfigKey(JBApp.JBAppConfigKey.values()[configType - 1]);
                config.setValue(value);
                JBApp.updateAppConfig(config);
                success("设置成功");
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "configs", help = "查看所有config的配置")
    public void showConfigs() {
        if (context.isAppAvailable()) {
            try {
                for (JBApp.JBAppConfigKey key : JBApp.JBAppConfigKey.values()) {
                    String value = JBApp.getAppConfig(key);
                    message(key.getName() + "(" + key.getKey() + ") : " + value);
                }
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    private List<String> getConfigTypes() {
        List<String> list = new ArrayList<>();
        for (JBApp.JBAppConfigKey type : JBApp.JBAppConfigKey.values()) {
            list.add(type.getName() + " (" + type.getKey() + ")");
        }
        return list;
    }

}
