package com.datastax.astra.bash;

import java.io.FileOutputStream;
import java.io.IOException;

import com.datastax.astra.shell.AstraCli;
import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.GlobalUsageGenerator;
import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;

public class GenerateBashCompletion {

    public static void main(String[] args) {
        Cli<Runnable> cli = new Cli<Runnable>(AstraCli.class);
        GlobalUsageGenerator<Runnable> helpGenerator = new BashCompletionGenerator<>();
        try {
            helpGenerator.usage(cli.getMetadata(), new FileOutputStream("dist/astra-init.sh"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



