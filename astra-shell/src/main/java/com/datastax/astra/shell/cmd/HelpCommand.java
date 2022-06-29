package com.datastax.astra.shell.cmd;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.help.Help;

/**
 * Display help.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <T>
 */
@Command(name = "help", description = "Display help information")
public class HelpCommand<T> extends Help<T> {
   
    /** {@inheritDoc} */
    @Override
    public void run() {
        super.run();
    }
}