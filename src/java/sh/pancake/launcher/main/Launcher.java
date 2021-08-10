/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.main;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import sh.pancake.launcher.PancakeLauncher;

@Command(name = "pancake-launcher")
public class Launcher implements Callable<Void> {

    @Parameters(index = "0", description = "server file to launch")
    File server;

    @Parameters(index = "1..*", description = "server arguments")
    String[] args;

    @Override
    public Void call() throws Exception {
        PancakeLauncher.launch(server, args);
        
        return null;
    }

    public static void main(String[] args) throws Throwable {
        new CommandLine(new Launcher()).execute(args);
    }
    
}
