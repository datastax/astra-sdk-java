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
@Command(name = "roles", description = "Display the list of Roles in an organization")
public class ShowRolesCommand extends BaseCommand<ShowRolesCommand> {

    private static final String ROLE_ID          = "Role Id";
    private static final String ROLE_NAME        = "Role Name";
    private static final String ROLE_DESCRIPTION = "Description";
    
    /** {@inheritDoc} */
    public void execute() {
        ShellTable sht = new ShellTable();
        sht.addColumn(ROLE_ID, 37);
        sht.addColumn(ROLE_NAME, 20);
        sht.addColumn(ROLE_DESCRIPTION, 20);
        getApiDevopsOrganizations().roles().forEach(role -> {
         Map <String, String> rf = new HashMap<>();
         rf.put(ROLE_ID, role.getId());
         rf.put(ROLE_NAME, role.getName());
         rf.put(ROLE_DESCRIPTION, role.getPolicy().getDescription());
         sht.getCellValues().add(rf);
        });
        sht.show();
    }
}
