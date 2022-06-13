package com.datastax.astra.shell.cmd.iam;

import java.util.HashMap;
import java.util.Map;

import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Command;

/**
 * Display roles.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "roles", description = "Display the list of Roles in an organization")
public class ShowRolesCommand extends BaseCommand<ShowRolesCommand> {

    /** {@inheritDoc} */
    public void execute() {
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("Role Id");
        sht.getColumnTitlesNames().add("Role Name");
        sht.getColumnTitlesNames().add("Description");
        sht.getColumnSize().put("Role Id", 37);
        sht.getColumnSize().put("Role Name", 20);
        sht.getColumnSize().put("Description", 20);
        ShellContext.getApiDevopsOrganizations()
                    .roles().forEach(role -> {
         Map <String, String> rf = new HashMap<>();
         rf.put("Role Id", role.getId());
         rf.put("Role Name", role.getName());
         rf.put("Description", role.getPolicy().getDescription());
         sht.getCellValues().add(rf);
        });
        sht.show();
    }
}
