package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.*;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.DateUtil;
import com.javabaas.shell.util.FieldUtil;
import com.javabaas.shell.util.table.AsciiTableRenderer;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.WidthFixedColumns;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.javabaas.shell.util.Print.*;

/**
 * Created by Staryet on 15/8/21.
 * <p>
 * 对象命令
 */
@Component
public class ObjectCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliCommand(value = "add", help = "创建对象")
    public void add(@CliOption(key = {""}, mandatory = true, help = "Object by json.") final String string)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            String className = context.getCurrentClass();
            try {
                Map<String, Object> map = JBUtils.readValue(string, Map.class);
                JBObject object = new JBObject(className);
                JBUtils.copyPropertiesFromMapToJBObject(object, map);
                object.save();
                message("对象创建.");
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "update", help = "更新对象")
    public void update(@CliOption(key = {""}, mandatory = true, help = "Input") final String input)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            String[] inputs = input.split(" ");
            if (inputs.length < 2) {
                error("需要json对象!");
                return;
            }
            try {
                String id = inputs[0];
                String className = context.getCurrentClass();
                JBObject object = JBObject.createWithOutData(className, id);
                JBUtils.copyPropertiesFromMapToJBObject(object, JBUtils.readValue(inputs[1], Map.class));
                object.save();
                success("对象更新");
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "get", help = "获取对象")
    public void get(@CliOption(key = {""}, mandatory = true, help = "Object id.") final String id)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            try {
                String className = context.getCurrentClass();
                JBQuery query = new JBQuery(className);
                JBObject object = query.get(id);
                message(object);
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "list", help = "查询对象")
    public void list(@CliOption(key = {""}, help = "Query condition.") final String where,
                     @CliOption(key = {"skip"}, unspecifiedDefaultValue = "0", specifiedDefaultValue = "0") final
                     String skip)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            try {
                String className = context.getCurrentClass();
                JBQuery query = new JBQuery(className);
                if (!JBUtils.isEmpty(skip)) {
                    query.setSkip(Integer.parseInt(skip));
                }
                if (!JBUtils.isEmpty(where)) {
                    query.setWhereSting(where);
                }
                List<JBObject> list = query.find();
                list.forEach(object -> message(object));
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "table", help = "表格展示")
    public void table(@CliOption(key = {""}, help = "Query condition.") final String where,
                      @CliOption(key = {"skip"}, unspecifiedDefaultValue = "0", specifiedDefaultValue = "0") final
                      String skip,
                      @CliOption(key = {"t"}, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String
                              time,
                      @CliOption(key = {"p"}, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String
                              plat,
                      @CliOption(key = {"a"}, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String
                              acl,
                      @CliOption(key = {"s"}, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String
                              single)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            try {
                String className = context.getCurrentClass();
                JBQuery query = new JBQuery(className);
                if (!JBUtils.isEmpty(skip)) {
                    query.setSkip(Integer.parseInt(skip));
                }
                if (!JBUtils.isEmpty(where)) {
                    query.setWhereSting(where);
                }
                List<JBObject> list = query.find();

                //创建表格
                V2_AsciiTable at = new V2_AsciiTable();
                at.addRule();
                //控制列宽度
                WidthFixedColumns width = new WidthFixedColumns();
                List<JBField> fields = JBField.list(className);
                //整理表头
                List<Object> headers = new LinkedList<>();
                List<Object> types = new LinkedList<>();
                headers.add("id");
                types.add("<STRING>");
                width.add(34);
                if (time.equals("1")) {
                    //显示时间
                    headers.add("createdAt");
                    types.add("<DATE>");
                    width.add(21);
                }

                //自定义字段
                fields.forEach(field -> {
                    headers.add(field.getName());
                    types.add(FieldUtil.getFieldType(field.getType()));
                    width.add(20);
                });
                at.addRow(headers.toArray());
                at.addRow(types.toArray());
                at.addStrongRule();
                list.forEach(baasObject -> {
                    List<Object> cols = new LinkedList<>();
                    cols.add(baasObject.getObjectId());
                    if (time.equals("1")) {
                        cols.add(DateUtil.format(Long.valueOf(baasObject.getCreatedAt())));
                    }
                    if (plat.equals("1")) {
                        cols.add("");
                    }
                    fields.forEach(field -> {
                        Object value = baasObject.get(field.getName());
                        if (value == null) {
                            cols.add("");
                        } else {
                            cols.add(value);
                        }
                    });
                    at.addRow(cols.toArray());
                    at.addRule();
                });
                AsciiTableRenderer rend = new AsciiTableRenderer();
                rend.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
                rend.setWidth(width);
                if (single.equals("0")) {
                    message(rend.render(at));
                } else {
                    //单行显示
                    message(rend.render(at, true));
                }

            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "del", help = "删除对象")
    public void delete(@CliOption(key = {""}, mandatory = true, help = "Object id.") final String id) {
        if (context.isClassAvailable()) {
            String className = context.getCurrentClass();
            try {
                JBObject object = JBObject.createWithOutData(className, id);
                object.delete();
                success("对象删除.");
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "url", help = "显示对象URL")
    public void url() {
        if (context.isClassAvailable()) {
            try {
                String className = context.getCurrentClass();
                String host = context.getCurrentServer().getHost();
                System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("GET    ").reset().a(host + "/object/" + className));
                System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("GET    ").reset().a(host + "/object/" + className + "/{id}"));
                System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("POST    ").reset().a(host + "/object/" + className));
                System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("PUT    ").reset().a(host + "/object/" + className + "/{id}"));
                System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("DELETE    ").reset().a(host + "/object/" + className + "/{id}"));
            } catch (Exception ignored) {
            }
        }
    }

    @CliCommand(value = "count", help = "对象计数")
    public void count(@CliOption(key = {""}, help = "Query condition.") final String where)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            try {
                String className = context.getCurrentClass();
                JBQuery query = new JBQuery(className);
                query.setWhereSting(where);
                int count = query.count();
                message(count);
            } catch (JBException e) {
                message(e.getMessage());
            }
        }
    }

}
