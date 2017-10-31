package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBException;
import com.javabaas.shell.common.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.javabaas.shell.util.Print.error;
import static com.javabaas.shell.util.Print.message;

/**
 * Created by Staryet on 15/8/20.
 * <p/>
 * 应用状态命令
 */
@Component
public class ApiStatCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliCommand(value = "stat", help = "显示统计")
    public void stat() {
        if (context.isAppAvailable()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = new GregorianCalendar();
            Date to = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Date from = calendar.getTime();
            String fromString = simpleDateFormat.format(from);
            String toString = simpleDateFormat.format(to);
            //显示列表
            JBApp.JBApiStat apiStat = new JBApp.JBApiStat(null, null, null, fromString, toString);
            try {
                List<Long> list = JBApp.getApiStat(apiStat);
                message(list);
            } catch (JBException e) {
                error(e.getMessage());
            }
        }
    }

}
