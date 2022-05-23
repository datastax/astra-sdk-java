package com.datastax.astra;

import com.datastax.astra.processor.ConnectProcessor;
import com.datastax.astra.processor.ExitProcessor;
import com.datastax.astra.processor.HelpProcessor;
import com.datastax.astra.processor.QuitProcessor;
import com.datastax.astra.processor.ShowProcessor;

/**
 * A Command will be an interface (pattern strategy). This Enum hold the catalog
 * of the commands.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum CommandTypes {
    
    /* Commands */
    connect(new ConnectProcessor()),
    help(new HelpProcessor()),
    exit(new ExitProcessor()),
    quit(new QuitProcessor()),
    q(new QuitProcessor()),
    show(new ShowProcessor());

    // -- ORG --

    // List organizations: ls organizations, ls org

    // Select an organization: organization <org>, org <org>
    
    // Show current context: info
    
    // List roles, show role details
    
    // List users: ls users, show users
    
    // Invite user: invite
    
    // List Databases: ls db, ls databases, show databases

    // Select a DB: database <db>, db <db>
    
    
    // -- DB --

    // List regions: ls regions, show regions
    
    // List keyspaces: ls keyspaces, ls ks, show keyspaces
    
    // Select a keyspace: ks <ks>, use <ks>
    
    // delete
    
    
    /** Command processor. */
    private final CommandProcessor processor;
    
    /**
     * Definition of a command.
     *
     * @param cmd
     *      command keywork
     * @param command
     *      command processor
     */
    private CommandTypes(CommandProcessor command) {
        this.processor = command;
    }
    
    /**
     * Command processor.
     * 
     * @return
     *      current command processor.
     */
    public CommandProcessor getProcessor() {
        return processor;
        
    }
    
}
