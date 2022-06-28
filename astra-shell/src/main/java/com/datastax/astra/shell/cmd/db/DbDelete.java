package com.datastax.astra.shell.cmd.db;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.cmd.BaseShellCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Delete a DB is exist
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name = BaseCommand.DELETE, 
    description = "Delete an existing database")
public class DbDelete extends BaseShellCommand {
    
    /**
     * Database name or identifier
     */
    @Required
    @Arguments(title = "DB", description = "Database name or identifier")
    public String databaseId;
    
    /** {@inheritDoc} */
    public ExitCode execute() {
        return ExitCode.SUCCESS;
        /*
        retrieveDatabaseClient(databaseId).ifPresent(dbClient -> {
            dbClient.delete();
            LoggerShell.info("Deleting Database " + databaseId + " (async)");
            LoggerShell.info("Use 'show dbs' or 'show db <dbId>' to see status");
        });*/
    }
    
}
