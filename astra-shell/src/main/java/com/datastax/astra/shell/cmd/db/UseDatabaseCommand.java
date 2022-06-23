package com.datastax.astra.shell.cmd.db;

import static com.datastax.astra.shell.cmd.db.DatabaseCommandUtils.retrieveDatabaseClient;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Make the db part of the context and the prompt
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
        name = DatabaseCommandUtils.DB, 
        description = "Scope a database")
public class UseDatabaseCommand extends BaseCommand<UseDatabaseCommand> {
    
    @Required
    @Arguments(title = "DB", description = "Database name or identifier")
    public String databaseId;
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        retrieveDatabaseClient(databaseId).ifPresent(dbClient -> {
            ShellContext.getInstance().useDatabase(dbClient.find().get());
            LoggerShell.info("Database '" + databaseId + "' is now selected with region '" + ShellContext.getInstance().getDatabaseRegion() + "'");
        });
    }
}

  
