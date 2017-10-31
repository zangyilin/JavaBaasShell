package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PromptUtil;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 类命令
 */
@Component
public class ClassCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliCommand(value = "classes", help = "显示类列表")
    public void find() {
        if (context.isAppAvailable()) {
            try {
                //显示列表
                List<JBClazz> list = JBClazz.list();
                list.forEach(clazz -> System.out.println(clazz.getName() + "(" + clazz.getCount() + ")"));
            } catch (JBException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
            }
        }
    }

    @CliCommand(value = "set", help = "设置当前类")
    public void set(@CliOption(key = {""}, help = "class name") final String name) {
        if (context.isAppAvailable()) {
            if (name == null) {
                List<JBClazz> list = JBClazz.list();
                List<String> values = new LinkedList<>();
                for (JBClazz clazz : list) {
                    values.add(clazz.getName());
                }
                int index = PromptUtil.choose(values, "请选择类:", 0);
                if (index == 0) {
                    //重置当前类
                    context.setCurrentClass(null);
                } else {
                    String className = list.get(index - 1).getName();
                    context.setCurrentClass(list.get(index - 1).getName());
                    System.out.println(Ansi.ansi().a("设置当前类为 ").fg(Ansi.Color.GREEN).a(className).reset());
                }
            } else {
                try {
                    JBClazz.get(name);
                    System.out.println(Ansi.ansi().a("设置当前类为 ").fg(Ansi.Color.GREEN).a(name).reset());
                    context.setCurrentClass(name);
                } catch (JBException e) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
                } catch (HttpClientErrorException e) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
                }
            }
        }
    }

    @CliCommand(value = "class del", help = "删除类")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
        if (context.isAppAvailable()) {
            if (PromptUtil.check("是否确认删除类?")) {
                try {
                    JBClazz clazz = new JBClazz(name);
                    clazz.delete();
                    context.setCurrentClass(null);
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("删除成功").reset());
                } catch (JBException e) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
                }
            }
        }
    }

    @CliCommand(value = "class add", help = "添加类")
    public void add(@CliOption(key = {""}) final String name) {
        if (context.isAppAvailable()) {
            try {
                String className;
                if (JBUtils.isEmpty(name)) {
                    className = PromptUtil.string("请输入类名称:");
                    if (JBUtils.isEmpty(className)) {
                        return;
                    }
                } else {
                    className = name;
                }
                JBClazz clazz = new JBClazz(className);
                clazz.save();
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("类创建成功").reset());
            } catch (JBException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "class export", help = "导出类结构")
    public void export(@CliOption(key = {""}, mandatory = true) final String name) {
        if (context.isAppAvailable()) {
            try {
                JBClazz.JBClazzExport clazzExport = JBClazz.export(name);
                System.out.println(JBUtils.writeValueAsString(clazzExport));
            } catch (JBException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "class import", help = "导入类结构")
    public void importData(@CliOption(key = {""}, help = "clazz", mandatory = true) final String clazz) {
        if (context.isAppAvailable()) {
            try {
                JBClazz.importData(clazz);
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("类导入成功").reset());
            } catch (JBException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

}
