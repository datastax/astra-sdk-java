package com.datastax.astra.shell.cmd.iam;

import java.util.HashMap;
import java.util.Map;

import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

/**
 * Display roles.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "users", description = "Display the list of users in an organization")
public class ShowUsersCommands extends BaseCommand<ShowUsersCommands> {
    
    private static final String USER_ID     = "User Id";
    private static final String USER_EMAIL  = "User Email";
    private static final String USER_STATUS = "Status";
    
    /** {@inheritDoc} */
    public void execute() {
        ShellTable sht = new ShellTable();
        sht.addColumn(USER_ID, 37);
        sht.addColumn(USER_EMAIL, 20);
        sht.addColumn(USER_STATUS, 20);
        getApiDevopsOrganizations().users().forEach(user -> {
         Map <String, String> rf = new HashMap<>();
         rf.put(USER_ID, user.getUserId());
         rf.put(USER_EMAIL, user.getEmail());
         rf.put(USER_STATUS, user.getStatus().name());
         sht.getCellValues().add(rf);
        });
        sht.show();
    }
}
