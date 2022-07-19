package com.datastax.astra.shell.cmd;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Command;

/**
 * Unselect an entity (database)
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "quit", description = "Remove scope focus on an entity (prompt changed).")
public class QuitCommand extends BaseShellCommand {

    /** {@inheritDoc} */
    @Override
    public ExitCode execute() {
        if (null != ctx().getDatabase()) {
            ctx().exitDatabase();
            return ExitCode.SUCCESS;
        }
        LoggerShell.warning("You have no base selected.");
        return ExitCode.NOT_FOUND;
    }

}
