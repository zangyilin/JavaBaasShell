package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBField;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.entity.JBFieldType;
import com.javabaas.shell.util.PromptUtil;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static com.javabaas.shell.util.Print.error;
import static com.javabaas.shell.util.Print.success;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 字段命令
 */
@Component
public class FieldCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    /**
     * 显示指定类的属性列表
     */
    @CliCommand(value = "fields", help = "显示字段列表")
    public void find() {
        if (context.isClassAvailable()) {
            try {
                String className = context.getCurrentClass();
                List<JBField> list = JBField.list(className);
                list.forEach(baasField -> {
                    String typeString;
                    switch (baasField.getType()) {
                        case JBFieldType.STRING:
                            typeString = "<STRING>  ";
                            break;
                        case JBFieldType.NUMBER:
                            typeString = "<NUMBER>  ";
                            break;
                        case JBFieldType.BOOLEAN:
                            typeString = "<BOOLEAN> ";
                            break;
                        case JBFieldType.DATE:
                            typeString = "<DATE>    ";
                            break;
                        case JBFieldType.FILE:
                            typeString = "<FILE>    ";
                            break;
                        case JBFieldType.OBJECT:
                            typeString = "<OBJECT>  ";
                            break;
                        case JBFieldType.ARRAY:
                            typeString = "<ARRAY>   ";
                            break;
                        case JBFieldType.POINTER:
                            typeString = "<POINTER> ";
                            break;
                        default:
                            typeString = "<GEOPOINT>";
                            break;
                    }
                    String internalString = baasField.isInternal() ? "I" : " ";
                    String securityString = baasField.isSecurity() ? "S" : " ";
                    String notnullString = baasField.isNotnull() ? "N" : " ";
                    String readonlyString = baasField.isReadonly() ? "R" : " ";
                    System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(internalString).fg(Ansi.Color.RED).a(securityString).fg(Ansi
                            .Color

                            .GREEN).a(notnullString).fg(Ansi.Color.BLUE).a(readonlyString).fg(Ansi.Color.CYAN).a(typeString).reset().a(baasField.getName()));
                });
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "field del", help = "删除字段")
    public void delete(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            String className = context.getCurrentClass();
            //显示类信息
            if (PromptUtil.check("是否确认删除字段?")) {
                JBField field = new JBField();
                field.setClazz(new JBClazz(className));
                field.setName(fieldName);
                try {
                    field.delete();
                    success("删除成功");
                } catch (JBException e) {
                    error(e.getMessage());
                }
            }
        }
    }

    @CliCommand(value = "field add", help = "添加字段")
    public void add(@CliOption(key = {""}) final String name) {
        if (context.isClassAvailable()) {
            String className = context.getCurrentClass();
            try {
                String fieldName;
                if (JBUtils.isEmpty(name)) {
                    fieldName = PromptUtil.string("请输入字段名称:");
                    if (JBUtils.isEmpty(fieldName)) {
                        return;
                    }
                } else {
                    fieldName = name;
                }
                List<String> types = getFieldTypes();
                int type = PromptUtil.choose(types, "请选择FieldType", 1);
                if (type == 0) {
                    System.out.println("创建字段失败");
                    return;
                }
                JBField field = new JBField(type, fieldName);
                field.setClazz(new JBClazz(className));
                field.save();
                success("创建字段成功");
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "field +n", help = "设置必填字段")
    public void setNotnull(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            setNotnull(fieldName, true);
        }
    }

    @CliCommand(value = "field -n", help = "取消必填字段")
    public void setNoNotnull(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            setNotnull(fieldName, false);
        }
    }

    @CliCommand(value = "field +r", help = "设置只读字段")
    public void setReadonly(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            setReadonly(fieldName, true);
        }
    }

    @CliCommand(value = "field -r", help = "取消只读字段")
    public void setNoReadonly(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            setReadonly(fieldName, false);
        }
    }

    @CliCommand(value = "field +s", help = "设置安全字段 安全字段必须需要 master 管理权限才可以修改")
    public void setSecurity(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            setSecurity(fieldName, true);
        }
    }

    @CliCommand(value = "field -s", help = "取消安全字段")
    public void setNoSecurity(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        if (context.isClassAvailable()) {
            setSecurity(fieldName, false);
        }
    }

    private void setSecurity(String fieldName, boolean security) {
        String className = context.getCurrentClass();
        try {
            // 查询field
            JBField field = JBField.get(className, fieldName);
            field.setSecurity(security);
            field.update();
            success("更新成功");
        } catch (JBException e) {
            error(e.getMessage());
        }
    }

    private void setReadonly(String fieldName, boolean readonly) {
        String className = context.getCurrentClass();
        try {
            JBField field = JBField.get(className, fieldName);
            field.setReadonly(readonly);
            field.update();
            success("更新成功");
        } catch (JBException e) {
            error(e.getMessage());
        }
    }

    private void setNotnull(String fieldName, boolean notnull) {
        String className = context.getCurrentClass();
        try {
            JBField field = JBField.get(className, fieldName);
            field.setNotnull(notnull);
            field.update();
            success("更新成功");
        } catch (JBException e) {
            error(e.getMessage());
        }
    }

    @CliCommand(value = "field type", help = "显示字段类型列表")
    public void fieldType() {
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("1").fg(Ansi.Color.CYAN).a(" STRING").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("2").fg(Ansi.Color.CYAN).a(" NUMBER").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("3").fg(Ansi.Color.CYAN).a(" BOOLEAN").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("4").fg(Ansi.Color.CYAN).a(" DATE").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("5").fg(Ansi.Color.CYAN).a(" FILE").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("6").fg(Ansi.Color.CYAN).a(" OBJECT").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("7").fg(Ansi.Color.CYAN).a(" ARRAY").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("8").fg(Ansi.Color.CYAN).a(" POINTER").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("9").fg(Ansi.Color.CYAN).a(" GEOPOINT").reset());
    }

    private LinkedList<String> getFieldTypes() {
        LinkedList<String> list = new LinkedList<>();
        list.add("STRING");
        list.add("NUMBER");
        list.add("BOOLEAN");
        list.add("DATE");
        list.add("FILE");
        list.add("OBJECT");
        list.add("ARRAY");
        list.add("POINTER");
        list.add("GEOPOINT");
        return list;
    }

}
