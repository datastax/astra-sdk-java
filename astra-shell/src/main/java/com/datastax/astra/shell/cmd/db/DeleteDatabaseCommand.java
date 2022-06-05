package com.datastax.astra.shell.cmd.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.databases.DatabaseClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Delete a DB is exist
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "db", description = "Delete a new database")
public class DeleteDatabaseCommand extends BaseCommand<DeleteDatabaseCommand> {
    
    @Required
    @Arguments(title = "id", description = "Db name or id")
    public List<String> arguments = new ArrayList<>();
    
    /** {@inheritDoc} */
    public void execute() {
        if (arguments.size() != 1) {
            Out.print("Invalid arguments, please use 'role <dbNamw | dbId>'", TextColor.RED);
        } else {
            List<Database> dbs = getApiDevopsDb()
                    .databasesNonTerminatedByName(arguments.get(0))
                    .collect(Collectors.toList());
            if (dbs.size() > 1) {
                Out.error("There are '" + dbs.size() + "' dbs with this name.");
            } else if (1 == dbs.size()) {
                DatabaseClient dbc = new DatabaseClient(getApiDevopsDb(), dbs.get(0).getId());
                String dbId = dbc.find().get().getId();
                dbc.delete();
                Out.info("Deleting Database " + dbId);
            } else {
                // By name gave nothing
                DatabaseClient dbc = new DatabaseClient(getApiDevopsDb(), arguments.get(0));
                if (dbc.exist()) {
                    dbc.delete();
                    Out.info("Deleting Database " + arguments.get(0));
                } else {
                    Out.error("Invalid database identifier.");
                    Out.info("To get status: astra show db < dbName | dbId >");
                }
            }
        }
    }

}
