package com.datastax.astra.shell.cmd.shell;

import java.io.IOException;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCliCommand;
import com.datastax.astra.shell.utils.CqlShellUtils;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Command;

/**
 * Start cqlSH (db must be selected first).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "cqlsh", description = "Start cqlSH (db must be selected first)")
public class CqlShCommand extends BaseCliCommand {

    /** {@inheritDoc} */
    //@Override
    public ExitCode execute() {
        if (null == ShellContext.getInstance().getDatabase()) {
            LoggerShell.warning("You have no base selected.");
        } else {
            LoggerShell.info("Launching CqlSh");
            try {
               CqlShellUtils.runCqlShellAstra(this,
                       ShellContext.getInstance().getToken(), 
                       ShellContext.getInstance().getDatabase().getId(), 
                       ShellContext.getInstance().getDatabaseRegion());
               Thread.sleep(1000);
            } catch (IOException e) {
                LoggerShell.error("An error occured " + e.getMessage());
            } catch (InterruptedException e) {
                LoggerShell.error("An error occured " + e.getMessage());
            }
            LoggerShell.info("Exiting Cqlsh");
            System.exit(0);
        }
        return ExitCode.SUCCESS;
    }

    
}
