package com.datastax.astra.shell.cmd.iam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.User;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.LoggerShell;
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
            LoggerShell.error("Invalid arguments, please use 'role <roleId | roleName>'");
        } else {
            String user = arguments.get(0);
            Optional<User> user1 = Optional.empty();
            try {
                user1 = getApiDevopsOrganizations().findUserByEmail(user);
                if (!user1.isPresent()) {
                    user1 = getApiDevopsOrganizations().user(user).find();
                }
           
                if (!user1.isPresent()) {
                    LoggerShell.error("User '" + user1 + "' has not been found.\n");
                } else {
                    ShellTable sht = new ShellTable();
                    
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
                    
                    LoggerShell.print("\nSummary:", Ansi.Color.MAGENTA);
                    sht.show();
                    
                    LoggerShell.println("\nDetails (json):", Ansi.Color.MAGENTA);
                    ShellPrinter.printObjectAsJson(user1.get(), Ansi.Color.YELLOW);
                }
            } catch(RuntimeException e) {
                LoggerShell.error("Cannot show user, technical error " + e.getMessage());
            }
        }
    }

}
