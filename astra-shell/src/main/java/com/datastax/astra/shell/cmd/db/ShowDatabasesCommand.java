package com.datastax.astra.shell.cmd.db;

import java.util.HashMap;
import java.util.Map;

import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

/**
 * Show Databases for an organization 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(
    name = DatabaseCommandUtils.DBS, 
    description = "Display the list of Databases in an organization")
public class ShowDatabasesCommand extends BaseCommand<ShowDatabasesCommand> {
    
    /**
     * Synonyms: show dbs | databases
     */
    @Command(name = "databases", description = "Display the list of Databases in an organization")
    public static final class ShowDatabasesCommandBis extends ShowDatabasesCommand {}
    
    /** {@inheritDoc} */
    public void execute() {

        // Setup
        ShellTable sht = new ShellTable();
        sht.addColumn("Name",    20);
        sht.addColumn("Id",      37);
        sht.addColumn("Regions", 20);
        sht.addColumn("Status",  15);

        // Fill data
        getApiDevopsDb()
                .databasesNonTerminated()
                .forEach(db -> {
            Map <String, String> rf = new HashMap<>();
            rf.put("Name",    db.getInfo().getName());
            rf.put("Id",      db.getId());
            rf.put("Regions", db.getInfo().getRegion());
            rf.put("Status",  db.getStatus().name());
            sht.getCellValues().add(rf);
        });
        sht.show();
    }

}
