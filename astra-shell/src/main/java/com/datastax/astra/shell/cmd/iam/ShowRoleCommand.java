package com.datastax.astra.shell.cmd.iam;

import java.util.Optional;

import org.fusesource.jansi.Ansi;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.LoggerShell;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Display role.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "role", description = "Show role details")
public class ShowRoleCommand extends BaseCommand<ShowRoleCommand> {
    
    @Required
    @Arguments(title = "ROLE", description = "Role name or identifier")
    public String role;
    
   
    /** {@inheritDoc} */
    public void execute() {
        Optional<Role> role1 = Optional.empty();
        try {
            role1 = getApiDevopsOrganizations().findRoleByName(role);
            if (!role1.isPresent()) {
                role1 = getApiDevopsOrganizations()
                         .role(role)
                         .find();
            }
            if (!role1.isPresent()) {
                LoggerShell.error("Role '" + role + "' has not been found.");
            } else {
                printRole(role1.get());
            }
        } catch(RuntimeException e) {
             LoggerShell.error("Cannot show role, technical error " + e.getMessage());
        }
    }
    
    /**
     * Print role summary and details.
     * 
     * @param r
     *      role
     */
    void printRole(Role r) {
        ShellTable sht = new ShellTable();
        sht.getColumnSize().put("Name", 15);
        sht.getColumnSize().put("Value", 40);
        sht.getColumnTitlesNames().add("Name");
        sht.getColumnTitlesNames().add("Value");
        sht.getCellValues().add(ShellTable.addProperty("Identifier", r.getId()));
        sht.getCellValues().add(ShellTable.addProperty("Name", r.getName()));
        sht.getCellValues().add(ShellTable.addProperty("Description", r.getPolicy().getDescription()));
        sht.getCellValues().add(ShellTable.addProperty("Effect", r.getPolicy().getEffect()));
        LoggerShell.println("\nSummary:", Ansi.Color.MAGENTA);
        sht.show();
        LoggerShell.println("\nDetails (json):", Ansi.Color.MAGENTA);
        ShellPrinter.printObjectAsJson(r, Ansi.Color.YELLOW);
    }
    
}
