package com.datastax.astra.shell.cmd;

import java.util.ArrayList;
import java.util.List;
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
    @Arguments(title = "role", description = "Role name or identifier")
    public List<String> arguments = new ArrayList<>();
   
    /** {@inheritDoc} */
    public void execute() {
        if (arguments.size() != 1) {
            Out.print("Invalid arguments, please use 'role <roleId | roleName>'", TextColor.RED);
        } else {
            String role = arguments.get(0);
            Optional<Role> role1 = Optional.empty();
            try {
                role1 = ShellContext.apiDevopsOrganizations().findRoleByName(role);
                if (!role1.isPresent()) {
                    role1 = ShellContext
                            .apiDevopsOrganizations()
                            .role(role)
                            .find();
                }
                if (!role1.isPresent()) {
                    Out.error("Role '" + role + "' has not been found.");
                } else {
                    ShellTable sht = new ShellTable();
                    sht.setColumnTitlesColor(TextColor.YELLOW);
                    sht.setCellColor(TextColor.WHITE);
                    sht.setTableColor(TextColor.CYAN);
                    sht.getColumnSize().put("Name", 15);
                    sht.getColumnSize().put("Value", 40);
                    sht.getColumnTitlesNames().add("Name");
                    sht.getColumnTitlesNames().add("Value");
                    sht.getCellValues().add(ShellTable.addProperty("Identifier", role1.get().getId()));
                    sht.getCellValues().add(ShellTable.addProperty("Name", role1.get().getName()));
                    sht.getCellValues().add(ShellTable.addProperty("Description", role1.get().getPolicy().getDescription()));
                    sht.getCellValues().add(ShellTable.addProperty("Effect", role1.get().getPolicy().getEffect()));
                    
                    Out.println("\nSummary:", TextColor.MAGENTA);
                    sht.show();
                    
                    Out.println("\nDetails (json):", TextColor.MAGENTA);
                    ShellPrinter.printObjectAsJson(role1.get(), TextColor.YELLOW);
                }
            } catch(RuntimeException e) {
                Out.error("Cannot show role, technical error " + e.getMessage());
            }
        }
    }
}
