package com.datastax.astra.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.datastax.astra.CommandProcessor;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;
import com.datastax.astra.cmd.Argument;
import com.datastax.astra.cmd.Arguments;
import com.datastax.astra.cmd.ShellContext;
import com.datastax.astra.cmd.ShellPrinter;
import com.datastax.astra.cmd.ShellTable;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.astra.sdk.organizations.domain.User;
import com.datastax.astra.sdk.utils.AstraRc;

/**
 * Operate command `show`
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShowProcessor implements CommandProcessor {

    private static String ERROR_MESSAGE = "Invalid syntax in command 'show': ";
    
    /**
     * Item to be listed with ls.
     */
    public static enum ShownItems { 
        databases, dbs,
        organizations, orgs,
        tokens, 
        user, users, 
        role, roles, 
        access_lists, acls,
        private_links, links,
        brokers, tenants
    };
        
    /**
     * Access the item list.
     */
    public List<String> items = Arrays
            .stream(ShownItems.values())
            .map(ShownItems::name)
            .collect(Collectors.toList());
    
    /** {@inheritDoc} */
    @Override
    public Arguments getArgs() {
        return new Arguments().addArgument(Argument
                .builder()
                .description("Item to be shown")
                .name("target")
                .fixedValues(items).build());
    }

    /** {@inheritDoc} */
    @Override
    public String getDocumentation() {
        return "Show items among " + items;
    }

    /** {@inheritDoc} */
    @Override
    public void process(String commandLine) {
        CommandLine cli = null;
        try {
            
            /**
             * Validation
             */
            cli = new DefaultParser().parse(getOptions(), commandLine.split(" "));
            if (cli.getArgList().size() < 2) {
                Out.error(ERROR_MESSAGE + " an argument is expected");
                this.printHelp(commandLine);
            }
            
            /**
             * Processing based on target. show <target>
             */
            switch(ShownItems.valueOf(cli.getArgs()[1])) {
                case orgs:
                case organizations:
                    showOrganizations();
                break;
                case dbs: 
                case databases:
                    showDatabases();
                break;
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
                case acls:
                case access_lists:
                break;
                
                case brokers:
                case tenants:
                break;
                
                case links:
                case private_links:
                break;
                default:
                    break;
            }
            
        } catch (ParseException e) {
            Out.error(ERROR_MESSAGE + e.getMessage());
            //e.printStackTrace();
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
     * Show databases.
     */
    public void showDatabases() {
        // Setup Tableshow 
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("Name");
        sht.getColumnTitlesNames().add("Id");
        sht.getColumnTitlesNames().add("Regions");
        sht.getColumnTitlesNames().add("Status");
        sht.getColumnSize().put("Name", 20);
        sht.getColumnSize().put("Id", 37);
        sht.getColumnSize().put("Regions", 20);
        sht.getColumnSize().put("Status", 15);
        // Fill data
        ShellContext
                .getInstance()
                .getAstraClient()
                .apiDevopsDatabases()
                .databasesNonTerminated()
                .forEach(db -> {
            Map <String, String> rf = new HashMap<>();
            rf.put("Name", db.getInfo().getName());
            rf.put("Id", db.getId());
            rf.put("Regions", db.getInfo().getRegion());
            rf.put("Status", db.getStatus().name());
            sht.getCellValues().add(rf);
        });
        sht.show();
    }
    
    /**
     * Show organizations
     */
    public void showOrganizations() {
        // Setup Table
        ShellTable sht = new ShellTable();
        sht.setColumnTitlesColor(TextColor.YELLOW);
        sht.setCellColor(TextColor.WHITE);
        sht.setTableColor(TextColor.CYAN);
        sht.getColumnTitlesNames().add("Organization Name");
        sht.getColumnTitlesNames().add("Token");
        sht.getColumnSize().put("Organization Name", 20);
        sht.getColumnSize().put("Token", 37);
        AstraRc arc = AstraRc.load();
        for (String org : arc.getSections().keySet()) {
            Map <String, String> rf = new HashMap<>();
            rf.put("Organization Name", org);
            rf.put("Token",arc.getSections().get(org).get(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN));
            sht.getCellValues().add(rf);
        }
        sht.show();
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
            role1 = ShellContext
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
