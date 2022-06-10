package com.datastax.astra.shell.cmd;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.help.Help;

@Command(name = "help", description = "Display help information")
public class HelpCommand<T> extends Help<T> {
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        super.run();
        /*
        try {
            if (!command.isEmpty()) {
                new CliGlobalUsageSummaryGenerator<T>(false)
                    .usage(global, System.out);
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating usage documentation", e);
        }*/
    }
}