package com.datastax.astra.shell.cmd;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.annotations.Command;

/**
 * Unselect an entity (database)
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "quit", description = "Remove scope focus on an entity (prompt changed).")
public class QuitCommand extends BaseCommand<QuitCommand> {

    /** {@inheritDoc} */
    @Override
    public void execute() {
        if (null != ShellContext.getInstance().getDatabase()) {
            ShellContext.getInstance().exitDatabase();
        } else {
            Out.warning("You have no base selected.");
        }
        
    }
    

}
