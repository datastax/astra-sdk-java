package com.datastax.astra.shell.cmd;

import org.fusesource.jansi.Ansi;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Command;

/**
 * Exit properly from the Shell.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "exit", description = "Exit program.")
public class ExitCommand implements Runnable {

    /**
     * Default constructor.
     */
    public ExitCommand() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void run() {
        LoggerShell.println("Bye", Ansi.Color.CYAN);
        System.exit(ExitCode.SUCCESS.getCode());
    }

}
