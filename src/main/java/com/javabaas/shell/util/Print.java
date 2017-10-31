package com.javabaas.shell.util;

import org.fusesource.jansi.Ansi;

/**
 * Created by Codi on 2017/10/27.
 */
public class Print {

    private Ansi ansi = Ansi.ansi();

    public static Print print() {
        return new Print();
    }

    public static void success(Object value) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(value).reset());
    }

    public static void error(Object value) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(value).reset());
    }

    public static void message(Object value) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.DEFAULT).a(value).reset());
    }

    public Print a(Object value) {
        ansi.a(value);
        return this;
    }

    public Print fg(Ansi.Color color) {
        ansi.fg(color);
        return this;
    }

    public Print bold() {
        ansi.bold();
        return this;
    }

    public Print boldOff() {
        ansi.boldOff();
        return this;
    }

    public void p() {
        System.out.println(ansi);
        ansi.reset();
    }

}
