package com.datastax.astra.shell.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.User;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.datastax.astra.shell.utils.ShellTable;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Display user.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "user", description = "Show user details")
public class ShowUserCommand extends BaseCommand<ShowUserCommand> {
    
    @Required
    @Arguments(title = "user", description = "User email or identifier")
    public List<String> arguments = new ArrayList<>();
    
    /** {@inheritDoc} */
    public void execute() {
        if (arguments.size() != 1) {
            Out.print("Invalid arguments, please use 'role <roleId | roleName>'", TextColor.RED);
        } else {
            String user = arguments.get(0);
            Optional<User> user1 = Optional.empty();
            try {
                user1 = ShellContext
                    .apiDevopsOrganizations()
                    .findUserByEmail(user);
                if (!user1.isPresent()) {
                    user1 = ShellContext
                            .apiDevopsOrganizations()
                            .user(user)
                            .find();
                }
           
                if (!user1.isPresent()) {
                    Out.error("User '" + user1 + "' has not been found.\n");
                } else {
                    ShellTable sht = new ShellTable();
                    sht.setColumnTitlesColor(TextColor.YELLOW);
                    sht.setCellColor(TextColor.WHITE);
                    sht.setTableColor(TextColor.CYAN);
                    sht.getColumnSize().put("Name", 15);
                    sht.getColumnSize().put("Value", 40);
                    sht.getColumnTitlesNames().add("Name");
                    sht.getColumnTitlesNames().add("Value");
                    sht.getCellValues().add(ShellTable.addProperty("Identifier", user1.get().getUserId()));
                    sht.getCellValues().add(ShellTable.addProperty("Email", user1.get().getEmail()));
                    sht.getCellValues().add(ShellTable.addProperty("Roles Count", String.valueOf(user1.get()
                            .getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList()).size())));
                    
                    Out.print("\nSummary:", TextColor.MAGENTA);
                    sht.show();
                    
                    Out.println("\nDetails (json):", TextColor.MAGENTA);
                    ShellPrinter.printObjectAsJson(user1.get(), TextColor.YELLOW);
                }
            } catch(RuntimeException e) {
                Out.error("Cannot show user, technical error " + e.getMessage());
            }
        }
    }

}
