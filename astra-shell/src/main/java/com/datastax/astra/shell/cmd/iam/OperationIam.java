package com.datastax.astra.shell.cmd.iam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.output.JsonOutput;
import com.datastax.astra.shell.utils.LoggerShell;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.datastax.astra.shell.utils.ShellTable;

/**
 * Utility class for command `eolw`
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class OperationIam {
    
    /** Column name. */
    private static final String COLUMN_ROLE_ID          = "Role Id";
    /** Column name. */
    private static final String COLUMN_ROLE_NAME        = "Role Name";
    /** Column name. */
    private static final String COLUMN_ROLE_DESCRIPTION = "Description";
    
    /** Column name. */
    private static final String COLUMN_USER_ID          = "User Id";
    /** Column name. */
    private static final String COLUMN_USER_EMAIL       = "User Email";
    /** Column name. */
    private static final String COLUMN_USER_STATUS      = "Status";
    
    /**
     * List Roles.
     * 
     * @param cmd
     *      current command
     * @return
     *      returned code
     */
    public static ExitCode listRoles(BaseCommand cmd) {
        ShellTable sht = new ShellTable();
        sht.addColumn(COLUMN_ROLE_ID, 37);
        sht.addColumn(COLUMN_ROLE_NAME, 20);
        sht.addColumn(COLUMN_ROLE_DESCRIPTION, 20);
        ShellContext.getInstance()
                    .getApiDevopsOrganizations()
                    .roles()
                    .forEach(role -> {
             Map <String, String> rf = new HashMap<>();
             rf.put(COLUMN_ROLE_ID, role.getId());
             rf.put(COLUMN_ROLE_NAME, role.getName());
             rf.put(COLUMN_ROLE_DESCRIPTION, role.getPolicy().getDescription());
             sht.getCellValues().add(rf);
        });
        ShellPrinter.printShellTable(sht, cmd.getFormat());
        return ExitCode.SUCCESS;
    }
    
    /**
     * List Roles.
     * 
     * @param cmd
     *      current command
     * @return
     *      returned code
     */
    public static ExitCode listUsers(BaseCommand cmd) {
        ShellTable sht = new ShellTable();
        sht.addColumn(COLUMN_USER_ID, 37);
        sht.addColumn(COLUMN_USER_EMAIL, 20);
        sht.addColumn(COLUMN_USER_STATUS, 20);
        ShellContext.getInstance()
                    .getApiDevopsOrganizations()
                    .users().forEach(user -> {
             Map <String, String> rf = new HashMap<>();
             rf.put(COLUMN_USER_ID, user.getUserId());
             rf.put(COLUMN_USER_EMAIL, user.getEmail());
             rf.put(COLUMN_USER_STATUS, user.getStatus().name());
             sht.getCellValues().add(rf);
        });
        ShellPrinter.printShellTable(sht, cmd.getFormat());
        return ExitCode.SUCCESS;
    }
    
    /**
     * Show Role details
     * @param cmd
     *      command
     * @param role
     *      role name
     * @return
     *      exit code
     */
    public static ExitCode showRole(BaseCommand cmd, String role) {
        try {
            Optional<Role> optRole = ShellContext
                    .getInstance()
                    .getApiDevopsOrganizations()
                    .findRoleByName(role);
            
            if (!optRole.isPresent()) {
                optRole = ShellContext
                        .getInstance()
                        .getApiDevopsOrganizations()
                        .role(role)
                        .find();
            }
            
            if (!optRole.isPresent()) {
                cmd.outputError(ExitCode.NOT_FOUND, "Role '" + role + "' has not been found.");
                return ExitCode.NOT_FOUND;
            }
            
            Role r = optRole.get();
            ShellTable sht = ShellTable.propertyTable(15, 40);
            sht.addPropertyRow("Identifier",    r.getId());
            sht.addPropertyRow("Name",          r.getName());
            sht.addPropertyRow("Description",   r.getPolicy().getDescription());
            sht.addPropertyRow("Effect",        r.getPolicy().getEffect());
            sht.addPropertyListRows("Resources", r.getPolicy().getResources());
            sht.addPropertyListRows("Actions", r.getPolicy().getActions());
            switch(cmd.getFormat()) {
                case human:
                    sht.show();
                case json:
                    LoggerShell.json(new JsonOutput(ExitCode.SUCCESS, "role show " + role, r));
                case csv:
                default:
                break;
            }
            
        } catch(RuntimeException e) {
            cmd.outputError(ExitCode.INTERNAL_ERROR,"Cannot show role, technical error " + e.getMessage());
            return ExitCode.INTERNAL_ERROR;
        }
        
        return ExitCode.SUCCESS;
    }
    

}
