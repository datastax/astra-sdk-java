package com.datastax.astra.shell.cmd.repl;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.annotations.Command;

/**
 * Exit properly from the Shell.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "exit", description = "Exit properly from the Shell.")
public class ExitCommand implements Runnable {

    /** {@inheritDoc} */
    @Override
    public void run() {
        Out.println("Bye", TextColor.CYAN);
        System.exit(ExitCode.SUCCESS.getCode());
    }

}
