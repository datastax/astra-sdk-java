package com.datastax.astra.shell.cmd.db;

import java.util.HashMap;
import java.util.Map;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

/**
 * Show Databases for an organization 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "dbs", description = "Display the list of Databases in an organization")
public class ShowDatabasesCommand extends BaseCommand<ShowDatabasesCommand> {
    
    /** {@inheritDoc} */
    public void execute() {
        ShellContext.getInstance().getAstraClient()
        .apiDevopsDatabases().databases();
        
        
        // Setup Tableshow 
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("Name");
        sht.getColumnTitlesNames().add("Id");
        sht.getColumnTitlesNames().add("Regions");
        sht.getColumnTitlesNames().add("Status");
        sht.getColumnSize().put("Name", 20);
        sht.getColumnSize().put("Id", 37);
        sht.getColumnSize().put("Regions", 20);
        sht.getColumnSize().put("Status", 15);
        // Fill data
        ShellContext
                .getInstance()
                .getAstraClient()
                .apiDevopsDatabases()
                .databasesNonTerminated()
                .forEach(db -> {
            Map <String, String> rf = new HashMap<>();
            rf.put("Name", db.getInfo().getName());
            rf.put("Id", db.getId());
            rf.put("Regions", db.getInfo().getRegion());
            rf.put("Status", db.getStatus().name());
            sht.getCellValues().add(rf);
        });
        sht.show();
    }

}
