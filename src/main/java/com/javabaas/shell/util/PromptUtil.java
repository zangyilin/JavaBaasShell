package com.javabaas.shell.util;

import com.javabaas.javasdk.JBUtils;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;

/**
 * Created by zangyilin on 2017/9/28.
 * 提示输入工具
 */
public class PromptUtil {

    /**
     * 从选项中选择
     *
     * @param values       选项
     * @param message      提示信息
     * @param defaultValue 默认值
     * @return 选择
     */
    public static int choose(List<String> values, String message, int defaultValue) {
        try {
            String input = showList(values, message);
            if (JBUtils.isEmpty(input)) {
                return defaultValue;
            }
            int result = Integer.parseInt(input);
            if (result > values.size() || result < 0) {
                System.out.println("输入错误 请重试");
                return choose(values, message, defaultValue);
            }
            return result;
        } catch (Exception e) {
            System.out.println("输入错误 请重试");
            return choose(values, message, defaultValue);
        }
    }

    public static boolean check(String message) {
        try {
            System.out.println(Ansi.ansi().fg(RED).a(message).fg(DEFAULT));
            String result = new ConsoleReader().readLine("(y/n)>");
            if (JBUtils.isEmpty(result)) {
                return false;
            } else {
                if (result.equalsIgnoreCase("y") || result.equalsIgnoreCase("yes")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String string(String message) {
        return string(message, null);
    }

    /**
     * 输入文本
     *
     * @param message      提示信息
     * @param defaultValue 默认值
     * @return 文本
     */
    public static String string(String message, String defaultValue) {
        try {
            ConsoleReader consoleReader = new ConsoleReader();
            System.out.println(Ansi.ansi().bold().fg(DEFAULT).a(message).boldOff());
            String prompt = JBUtils.isEmpty(defaultValue) ? ">" : ">(" + defaultValue + ")";
            String value = consoleReader.readLine(prompt);
            if (JBUtils.isEmpty(value)) {
                if (JBUtils.isEmpty(defaultValue)) {
                    //无默认值 空输入无效
                    System.out.println(Ansi.ansi().fg(RED).a("无效输入").fg(DEFAULT));
                    return null;
                } else {
                    //有默认值 无输入则为默认值
                    return defaultValue;
                }
            } else {
                return value;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static String showList(List<String> values, String message) {
        try {
            System.out.println(Ansi.ansi().bold().fg(DEFAULT).a(message).boldOff());
            for (int i = 1; i <= values.size(); i++) {
                System.out.println(Ansi.ansi().fg(WHITE).a(i + " ").fg(CYAN).a(values.get(i - 1)));
            }
            System.out.println(Ansi.ansi().fg(WHITE).a("0 ").fg(CYAN).a("取消").fg(DEFAULT));
            ConsoleReader consolereader = new ConsoleReader();
            return consolereader.readLine(">");
        } catch (Exception e) {
            return null;
        }
    }

}
