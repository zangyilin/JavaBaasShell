package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PromptUtil;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

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

    @CliAvailabilityIndicator({"classes", "set", "class add", "class del", "class export", "class import"})
    public boolean isAvailable() {
        return context.getCurrentApp() != null;
    }

    @CliCommand(value = "classes", help = "Show class list.")
    public void find() {
        try {
            //显示列表
            List<JBClazz> list = JBClazz.list();
            list.forEach(clazz -> System.out.println(clazz.getName() + "(" + clazz.getCount() + ")"));
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        }
    }

    @CliCommand(value = "set", help = "Set current class.")
    public void find(@CliOption(key = {""}, mandatory = false, help = "class name") final String name) {
        if (name == null) {
            //重置当前类
            context.setCurrentClass(null);
        } else {
            try {
                JBClazz.get(name);
                System.out.println(Ansi.ansi().a("Set current class to ").fg(Ansi.Color.GREEN).a(name).reset());
                context.setCurrentClass(name);
            } catch (JBException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "class del", help = "Delete class.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
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

    @CliCommand(value = "class add", help = "Add field.")
    public void add(@CliOption(key = {""}, mandatory = true) final String name) {
        try {
            JBClazz clazz = new JBClazz(name);
            clazz.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Class added.").reset());
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "class export", help = "Export tha class.")
    public void export(@CliOption(key = {""}, mandatory = true) final String name) {
        try {
            JBClazz.JBClazzExport clazzExport = JBClazz.export(name);
            System.out.println(JBUtils.writeValueAsString(clazzExport));
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "class import", help = "Import tha class.")
    public void importData(@CliOption(key = {""}, help = "clazz", mandatory = true) final String clazz) {
        try {
            JBClazz.importData(clazz);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Clazz imported.").reset());
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

}
