package com.datastax.astra.shell.cmd.iam;

import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.cmd.BaseShellCommand;
import com.github.rvesse.airline.annotations.Command;

/**
 * Display roles.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "roles", description = "Display the list of Roles in an organization")
public class ShowRolesCommand extends BaseShellCommand {

    private static final String ROLE_ID          = "Role Id";
    private static final String ROLE_NAME        = "Role Name";
    private static final String ROLE_DESCRIPTION = "Description";
    
    /** {@inheritDoc} */
    public ExitCode execute() {
        return ExitCode.SUCCESS;
        /*
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
        sht.show();*/
    }
}
