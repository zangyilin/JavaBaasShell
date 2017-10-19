package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBField;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.entity.JBSFieldType;
import com.javabaas.shell.util.ASKUtil;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 字段命令
 */
@Component
public class FieldCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliAvailabilityIndicator({"fields", "field add", "field del", "field r", "field nr", "field s", "field ns", "field type"})
    public boolean isAvailable() {
        return context.getCurrentClass() != null;
    }

    /**
     * 显示指定类的属性列表
     */
    @CliCommand(value = "fields", help = "Show field list in class.")
    public void find() {
        context.cancelDoubleCheck();
        try {
            String className = context.getCurrentClass();
            List<JBField> list = JBField.list(className);
            list.forEach(baasField -> {
                String typeString;
                switch (baasField.getType()) {
                    case JBSFieldType.STRING:
                        typeString = "<STRING>  ";
                        break;
                    case JBSFieldType.NUMBER:
                        typeString = "<NUMBER>  ";
                        break;
                    case JBSFieldType.BOOLEAN:
                        typeString = "<BOOLEAN> ";
                        break;
                    case JBSFieldType.DATE:
                        typeString = "<DATE>    ";
                        break;
                    case JBSFieldType.FILE:
                        typeString = "<FILE>    ";
                        break;
                    case JBSFieldType.OBJECT:
                        typeString = "<OBJECT>  ";
                        break;
                    case JBSFieldType.ARRAY:
                        typeString = "<ARRAY>   ";
                        break;
                    case JBSFieldType.POINTER:
                        typeString = "<POINTER> ";
                        break;
                    default:
                        typeString = "<GEOPOINT>";
                        break;
                }
                String internalString = baasField.isInternal() ? "I" : " ";
                String securityString = baasField.isSecurity() ? "S" : " ";
                String requiredString = baasField.isRequired() ? "R" : " ";
                System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(internalString).fg(Ansi.Color.RED).a(securityString).fg(Ansi.Color.GREEN).a(requiredString).fg(Ansi.Color.CYAN).a(typeString).reset().a(baasField.getName()));
            });
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        }
    }

    @CliCommand(value = "field del", help = "Delete field.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        //显示类信息
        try {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Do you really want to delete? (Y/N)"));
            context.setDoubleCheck(new DoubleCheckListener() {
                @Override
                public void confirm() {
                    JBField field = new JBField();
                    field.setClazz(new JBClazz(className));
                    field.setName(fieldName);
                    try {
                        field.delete();
                        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field deleted.").reset());
                    } catch (JBException e) {
                        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
                    }

                }

                @Override
                public void cancel() {

                }
            });
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "field add", help = "Add field.")
    public void add(@CliOption(key = {""}, mandatory = false) final String name) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            String fieldName;
            if (JBUtils.isEmpty(name)) {
                fieldName = ASKUtil.askString("请输入字段名称:");
                if (JBUtils.isEmpty(fieldName)) {
                    System.out.println("Faild Add Field!");
                    return;
                }
            } else {
                fieldName = name;
            }
            List<String> types = getFieldTypes();
            int type = ASKUtil.askNumber(types, "请选择Field type，默认为STRING", 1);
            if (type == 0) {
                System.out.println("Faild Add Field!");
                return;
            }
            JBField field = new JBField(type, fieldName);
            field.setClazz(new JBClazz(className));
            field.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field added.").reset());
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "field r", help = "Add field.")
    public void setRequired(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setRequired(fieldName, true);
    }

    @CliCommand(value = "field nr", help = "Add field.")
    public void setNoRequired(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setRequired(fieldName, false);
    }

    @CliCommand(value = "field s", help = "Add field.")
    public void setSecurity(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setSecurity(fieldName, true);
    }

    @CliCommand(value = "field ns", help = "Add field.")
    public void setNoSecurity(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setSecurity(fieldName, false);
    }

    private void setSecurity(String fieldName, boolean security) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            JBField field = new JBField();
            field.setClazz(new JBClazz(className));
            field.setName(fieldName);
            field.setSecurity(security);
            field.update();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field security updated.").reset());
        } catch (JBException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    private void setRequired(String fieldName, boolean required) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            JBField field = new JBField();
            field.setClazz(new JBClazz(className));
            field.setName(fieldName);
            field.setRequired(required);
            try {
                field.update();
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field required updated.").reset());
            } catch (JBException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset());
            }

        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "field type", help = "Show field Types.")
    public void fieldType() {
        context.cancelDoubleCheck();
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
        return list;
    }

}
