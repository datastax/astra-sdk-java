package com.datastax.astra.shell.cmd.use;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.cmd.BaseShellCommand;
import com.datastax.astra.shell.cmd.db.Db;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Make the db part of the context and the prompt
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
        name = Db.DB, 
        description = "Scope a database")
public class UseDb extends BaseShellCommand {
    
    @Required
    @Arguments(title = "DB", description = "Database name or identifier")
    public String databaseId;
    
    /** {@inheritDoc} */
    @Override
    public ExitCode execute() {
        //getDatabaseClient(databaseId).ifPresent(dbClient -> {
        //    ShellContext.getInstance().useDatabase(dbClient.find().get());
        //    LoggerShell.info("Database '" + databaseId + "' is now selected with region '" + ShellContext.getInstance().getDatabaseRegion() + "'");
        //});
        return ExitCode.SUCCESS;
    }

   
}

  
