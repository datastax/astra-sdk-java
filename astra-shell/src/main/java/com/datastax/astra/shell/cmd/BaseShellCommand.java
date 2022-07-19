package com.datastax.astra.shell.cmd;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.output.OutputFormat;

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
           // Keep history of commands and options of the shell
           ShellContext.getInstance().setCurrentShellCommand(this);
           this.format  = OutputFormat.human;
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
