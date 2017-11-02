package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import static com.javabaas.shell.util.Print.*;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 类信息命令
 */
@Component
public class ClassAclCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliCommand(value = "acls", help = "获取ACL信息")
    public void getACL() throws JsonProcessingException {
        if (context.isClassAvailable()) {
            String className = context.getCurrentClass();
            //显示类信息
            try {
                JBClazz clazz = JBClazz.get(className);
                message(clazz.getAcl());
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

    @CliCommand(value = "acl set", help = "设置类ACL")
    public void setACL(@CliOption(key = {""}, mandatory = true, help = "Object by json.") final String acl)
            throws JsonProcessingException {
        if (context.isClassAvailable()) {
            String className = context.getCurrentClass();
            try {
                JBClazz clazz = new JBClazz(className);
                JBClazz.JBClazzAcl clazzAcl = JBUtils.readValue(acl, JBClazz.JBClazzAcl.class);
                clazz.setAcl(clazzAcl);
                clazz.updateClazzAcl();
                success("ACL更新成功");
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

}
