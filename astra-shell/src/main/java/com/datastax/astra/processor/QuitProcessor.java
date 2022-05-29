package com.datastax.astra.processor;

import com.datastax.astra.CommandProcessor;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;
import com.datastax.astra.cmd.ShellContext;

/**
 * Exit the program. 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class QuitProcessor implements CommandProcessor {
  
    /** {@inheritDoc} */
    @Override
    public String getDocumentation() {
        return "Quit current selection";
    }

    /** {@inheritDoc} */
    @Override
    public void process(String commandLine) {
        if (ShellContext.getInstance().getDatabaseKeyspace() != null) {
            // Database was selected got back to database
            ShellContext.getInstance().setDatabaseKeyspace(null);
            Out.println("Quit keyspace [" + 
                    ShellContext.getInstance().getDatabaseKeyspace() + "]", TextColor.CYAN);
        } else if (ShellContext.getInstance().getDatabaseId() != null) {
            // Database was selected got back to organization
            ShellContext.getInstance().setDatabaseId(null);
            ShellContext.getInstance().setDatabaseName(null);
            ShellContext.getInstance().setDatabaseRegion(null);
            Out.println("Quit database [" + 
                    ShellContext.getInstance().getDatabaseName() + "]", TextColor.CYAN);
        } else {
            // Anything selected a quit will also exit
            new ExitProcessor().process(commandLine);
        }   
    }

}
