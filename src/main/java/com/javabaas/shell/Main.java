package com.javabaas.shell;

import org.fusesource.jansi.AnsiConsole;
import org.springframework.shell.Bootstrap;

import java.io.IOException;


/**
 * Created by Staryet on 15/8/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        AnsiConsole.systemInstall();
        Bootstrap.main(args);
    }

}