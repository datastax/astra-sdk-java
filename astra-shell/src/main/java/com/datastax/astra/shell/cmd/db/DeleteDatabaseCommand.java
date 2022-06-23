package com.datastax.astra.shell.cmd.db;

import static com.datastax.astra.shell.cmd.db.DatabaseCommandUtils.retrieveDatabaseClient;

import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Delete a DB is exist
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name = DatabaseCommandUtils.DB, 
    description = "Delete an existing database")
public class DeleteDatabaseCommand extends BaseCommand<DeleteDatabaseCommand> {
    
    /**
     * Synonyms: show dbs | databases
     */
    @Command(
        name = DatabaseCommandUtils.DATABASE, 
        description = "Delete an existing database")
    public static final class DeleteDatabaseCommandAlias1 extends DeleteDatabaseCommand {}
    
    
    /**
     * Database name or identifier
     */
    @Required
    @Arguments(title = "DB", description = "Database name or identifier")
    public String databaseId;
    
    /** {@inheritDoc} */
    public void execute() {
        retrieveDatabaseClient(databaseId).ifPresent(dbClient -> {
            dbClient.delete();
            LoggerShell.info("Deleting Database " + databaseId + " (async)");
            LoggerShell.info("Use 'show dbs' or 'show db <dbId>' to see status");
        });
    }
    
}
