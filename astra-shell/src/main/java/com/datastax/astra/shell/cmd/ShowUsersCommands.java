package com.datastax.astra.shell.cmd;

import java.util.HashMap;
import java.util.Map;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

/**
 * Display roles.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "users", description = "Display the list of users in an organization")
public class ShowUsersCommands extends BaseCommand<ShowUsersCommands> {

    
    /** {@inheritDoc} */
    public void execute() {
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("User Id");
        sht.getColumnTitlesNames().add("User Email");
        sht.getColumnTitlesNames().add("Status");
        sht.getColumnSize().put("User Id", 37);
        sht.getColumnSize().put("User Email", 20);
        sht.getColumnSize().put("Status", 20);
        ShellContext.getInstance()
                    .getAstraClient()
                    .apiDevopsOrganizations()
                    .users().forEach(user -> {
         Map <String, String> rf = new HashMap<>();
         rf.put("User Id", user.getUserId());
         rf.put("User Email", user.getEmail());
         rf.put("Status", user.getStatus().name());
         sht.getCellValues().add(rf);
        });
        sht.show();
    }
}
