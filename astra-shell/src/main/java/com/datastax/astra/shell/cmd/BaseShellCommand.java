package com.datastax.astra.shell.cmd;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.ShellContext;

/**
 * Base command.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class BaseShellCommand extends BaseCommand {
    
    /** {@inheritDoc} */
    public void run() {
       // As a shell command it should be initialized
       if (!ShellContext.getInstance().isInitialized()) {
           outputError(ExitCode.CONFLICT, "A shell command should have the connection set");
       } else {
           execute();
       }
    }
    
    /**
     * Return execution code (CLI)
     */
    public abstract ExitCode execute();
    
    /**
     * Get current context.
     * 
     * @return
     *      current context
     */
    protected ShellContext getContext() {
        return ShellContext.getInstance();
    }
   
}
