package com.datastax.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;

import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.User;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.datastax.astra.shell.utils.ShellTable;

/**
 * Operate command `show`
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShowProcessor {

    /** Error message. */
    private static String ERROR_MESSAGE = "Invalid syntax in command 'show': ";
    
    /**
     * Item to be listed with ls.
     */
    public static enum ShownItems { 
        db,
        dbs,
        org,
        orgs,
        token,
        tokens, 
        user, 
        users, 
        role, 
        roles, 
        acls,
        links,
        brokers, 
        tenants
    };

    /** {@inheritDoc} */
    public void process(String commandLine) {
        CommandLine cli = null;
        try {
            
           
            switch(ShownItems.valueOf(cli.getArgs()[1])) {
                case roles: 
                    showRoles();
                break;
                case role:
                    if (cli.getArgList().size() < 3) {
                        Out.error(ERROR_MESSAGE + " Use 'show role < name | id >' to get details for a role.");
                    }
                    showRole(cli.getArgs()[2]);
                break;
                case tokens: 
                    showTokens();
                break;
                case users: 
                    showUsers();
                break;
                case user:
                    if (cli.getArgList().size() < 3) {
                        Out.error(ERROR_MESSAGE + " Use 'show user < email | id >' to get details for a user.");
                    }
                    showUser(cli.getArgs()[2]);
                break;
               
            }
            
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            Out.error(ERROR_MESSAGE + "'" + cli.getArgs()[1] + "' is not a valid argument.");
            Out.print("\nPlease use ");
            Out.println(Arrays.stream(ShownItems.values())
                    .map(ShownItems::name)
                    .collect(Collectors.toList()).toString(), TextColor.CYAN);
        }
    }
    
    
    /**
     * Show roles.
     */
    public void showRoles() {
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
        ShellContext.getInstance()
                    .getAstraClient()
                    .apiDevopsOrganizations()
                    .roles().forEach(role -> {
         Map <String, String> rf = new HashMap<>();
         rf.put("Role Id", role.getId());
         rf.put("Role Name", role.getName());
         rf.put("Description", role.getPolicy().getDescription());
         sht.getCellValues().add(rf);
        });
        sht.show();
    }
    
    /**
     * Show a role in the console.
     * 
     * @param role
     *          role name or role id
     */
    public void showRole(String role) {
        Optional<Role> role1 = Optional.empty();
        try {
            role1 = ShellContext.getInstance()
                .apiDevopsOrganizations()
                .findRoleByName(role);
            if (!role1.isPresent()) {
                role1 = ShellContext
                        .apiDevopsOrganizations()
                        .role(role)
                        .find();
            }
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
        if (!role1.isPresent()) {
            Out.error("Role '" + role + "' has not been found.\n");
            Out.print("Please use one id or name from the following table:");
            showRoles();
        } else {
            ShellTable sht = new ShellTable();
            sht.setColumnTitlesColor(TextColor.YELLOW);
            sht.setCellColor(TextColor.WHITE);
            sht.setTableColor(TextColor.CYAN);
            sht.getColumnSize().put("Name", 15);
            sht.getColumnSize().put("Value", 40);
            sht.getColumnTitlesNames().add("Name");
            sht.getColumnTitlesNames().add("Value");
            sht.getCellValues().add(addProperty("Identifier", role1.get().getId()));
            sht.getCellValues().add(addProperty("Name", role1.get().getName()));
            sht.getCellValues().add(addProperty("Description", role1.get().getPolicy().getDescription()));
            sht.getCellValues().add(addProperty("Effect", role1.get().getPolicy().getEffect()));
            
            Out.print("\nSummary:", TextColor.MAGENTA);
            sht.show();
            
            Out.println("\nDetails (json):", TextColor.MAGENTA);
            ShellPrinter.printObjectAsJson(role1.get(), TextColor.YELLOW);
        }
    }
    
    /**
     * Show a role in the console.
     * 
     * @param user
     *          user email or user id
     */
    public void showUser(String user) {
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
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
        if (!user1.isPresent()) {
            Out.error("User '" + user1 + "' has not been found.\n");
            Out.print("Please use one id or email from the following table:");
            showUsers();
        } else {
            ShellTable sht = new ShellTable();
            sht.setColumnTitlesColor(TextColor.YELLOW);
            sht.setCellColor(TextColor.WHITE);
            sht.setTableColor(TextColor.CYAN);
            sht.getColumnSize().put("Name", 15);
            sht.getColumnSize().put("Value", 40);
            sht.getColumnTitlesNames().add("Name");
            sht.getColumnTitlesNames().add("Value");
            sht.getCellValues().add(addProperty("Identifier", user1.get().getUserId()));
            sht.getCellValues().add(addProperty("Email", user1.get().getEmail()));
            sht.getCellValues().add(addProperty("Roles Count", String.valueOf(user1.get()
                    .getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()).size())));
            
            Out.print("\nSummary:", TextColor.MAGENTA);
            sht.show();
            
            Out.println("\nDetails (json):", TextColor.MAGENTA);
            ShellPrinter.printObjectAsJson(user1.get(), TextColor.YELLOW);
        }
    }
    
    /**
     * Add property in a table.
     *
     * @param name
     *      property name
     * @param value
     *      property value
     * @return
     *      new row
     */
    private Map<String, String > addProperty(String name, String value) {
        Map <String, String> rf = new HashMap<>();
        rf.put("Name", name);
        rf.put("Value", value);
        return rf;
    }
    
    /**
     * Show users.
     */
    public void showUsers() {
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("User Id");
        sht.getColumnTitlesNames().add("User Email");
        sht.getColumnTitlesNames().add("Status");
        sht.getColumnSize().put("User Id", 37);
        sht.getColumnSize().put("User Email", 20);
        sht.getColumnSize().put("Status", 20);
        ShellContext.getInstance()
                    .getAstraClient()
                    .apiDevopsOrganizations()
                    .users().forEach(user -> {
         Map <String, String> rf = new HashMap<>();
         rf.put("User Id", user.getUserId());
         rf.put("User Email", user.getEmail());
         rf.put("Status", user.getStatus().name());
         sht.getCellValues().add(rf);
        });
        sht.show();
    }
    
    /**
     * Show tokens.
     */
    public void showTokens() {
    }
}
