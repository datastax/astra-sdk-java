package com.datastax.astra.shell.cmd.db;

import static com.datastax.astra.shell.cmd.db.DatabaseCommandUtils.retrieveDatabaseClient;

import org.fusesource.jansi.Ansi;

import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Display information relative to a db.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name = DatabaseCommandUtils.DB, 
    description = "Show details of a database")
public class ShowDatabaseCommand extends BaseCommand<ShowDatabaseCommand> {

    @Required
    @Arguments(title = "DB", description = "Database name or identifier")
    public String databaseId;
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        retrieveDatabaseClient(databaseId).ifPresent(dbClient -> {
            Database db = dbClient.find().get();
            ShellTable sht = new ShellTable();
            sht.setColumnTitlesColor(Ansi.Color.YELLOW);
            sht.setCellColor(Ansi.Color.WHITE);
            sht.setTableColor(Ansi.Color.CYAN);
            sht.getColumnSize().put("Name", 15);
            sht.getColumnSize().put("Value", 40);
            sht.getColumnTitlesNames().add("Name");
            sht.getColumnTitlesNames().add("Value");
            sht.getCellValues().add(ShellTable.addProperty("Identifier", db.getId()));
            sht.getCellValues().add(ShellTable.addProperty("Name", db.getInfo().getName()));
            sht.getCellValues().add(ShellTable.addProperty("Default Region", db.getInfo().getRegion()));
            sht.getCellValues().add(ShellTable.addProperty("Status", db.getStatus().toString()));
            sht.getCellValues().add(ShellTable.addProperty("Default Keyspace", db.getInfo().getKeyspace()));
            sht.getCellValues().add(ShellTable.addProperty("Creation Time", db.getCreationTime()));
            
            sht.show();
        });
    }

}
