package com.datastax.astra.shell.cmd;

import java.util.Optional;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
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
            role1 = ShellContext.getApiDevopsOrganizations().findRoleByName(role);
            if (!role1.isPresent()) {
                role1 = getApiDevopsOrg()
                         .role(role)
                         .find();
            }
            if (!role1.isPresent()) {
                Out.error("Role '" + role + "' has not been found.");
            } else {
                printRole(role1.get());
            }
        } catch(RuntimeException e) {
             Out.error("Cannot show role, technical error " + e.getMessage());
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
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnSize().put("Name", 15);
        sht.getColumnSize().put("Value", 40);
        sht.getColumnTitlesNames().add("Name");
        sht.getColumnTitlesNames().add("Value");
        sht.getCellValues().add(ShellTable.addProperty("Identifier", r.getId()));
        sht.getCellValues().add(ShellTable.addProperty("Name", r.getName()));
        sht.getCellValues().add(ShellTable.addProperty("Description", r.getPolicy().getDescription()));
        sht.getCellValues().add(ShellTable.addProperty("Effect", r.getPolicy().getEffect()));
        Out.println("\nSummary:", TextColor.MAGENTA);
        sht.show();
        Out.println("\nDetails (json):", TextColor.MAGENTA);
        ShellPrinter.printObjectAsJson(r, TextColor.YELLOW);
    }
    
}
