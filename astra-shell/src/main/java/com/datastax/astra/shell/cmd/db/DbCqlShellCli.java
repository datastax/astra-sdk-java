package com.datastax.astra.shell.cmd.db;

import static com.datastax.astra.shell.cmd.db.OperationsDb.downloadCloudSecureBundles;
import static com.datastax.astra.shell.utils.CqlShellUtils.installCqlShellAstra;

import java.io.IOException;
import java.util.Optional;

import com.datastax.astra.sdk.databases.DatabaseClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.cmd.BaseCliCommand;
import com.datastax.astra.shell.out.LoggerShell;
import com.datastax.astra.shell.out.ShellPrinter;
import com.datastax.astra.shell.utils.CqlShellUtils;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Start CqlSh for a DB.
 * 
 * https://cassandra.apache.org/doc/latest/cassandra/tools/cqlsh.html
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "cqlsh", description = "Start Cqlsh")
public class DbCqlShellCli extends BaseCliCommand {

    /**
     * Database name or identifier
     */
    @Required
    @Arguments(title = "DB", 
               description = "Database name or identifier")
    public String database;
    
    // -- Cqlsh --
    
    /** Cqlsh Options. */
    @Option(name = { "--version" }, 
            description = "Display information of cqlsh.")
    protected boolean cqlShOptionVersion = false;
    
    /** Cqlsh Options. */
    @Option(name= {"--debug"}, 
            description= "Show additional debugging information.")
    protected boolean cqlShOptionDebug = false;
    
    /** Cqlsh Options. */
    @Option(name = {"--encoding" }, title = "ENCODING", arity = 1,  
            description = "Output encoding. Default encoding: utf8.")
    protected String cqlshOptionEncoding;
    
    /** Cqlsh Options. */
    @Option(name = {"-e", "--execute" }, title = "STATEMENT", arity = 1,  
            description = "Execute the statement and quit.")
    protected String cqlshOptionExecute;
    
    /** Cqlsh Options. */
    @Option(name = {"-f", "--file" }, title = "FILE", arity = 1,  
            description = "Execute commands from a CQL file, then exit.")
    protected String cqlshOptionFile;
    
    /** Cqlsh Options. */
    @Option(name = {"-k", "--keyspace" }, title = "KEYSPACE", arity = 1,  
            description = "Authenticate to the given keyspace.")
    protected String cqlshOptionKeyspace;
    
    /** {@inheritDoc} */
    public ExitCode execute() {
        
        // Install Cqlsh for Astra and set permissions
        installCqlShellAstra();
        
        // Download SCB for target db if needed
        downloadCloudSecureBundles(database);    
        
        try {
            Optional<DatabaseClient> dbClient = OperationsDb.getDatabaseClient(database);
            if (dbClient.isPresent()) {
                Database db = dbClient.get().find().get();
                Process cqlShProc = CqlShellUtils.runCqlShellAstra(this, db);
                if (cqlShProc == null) ExitCode.INTERNAL_ERROR.exit();
                cqlShProc.waitFor();
                ShellPrinter.outputSuccess("Exiting Astra Cli");
            } else {
                return ExitCode.NOT_FOUND;
            }
        } catch (IOException e) {
            LoggerShell.error("Cannot start CQLSH");
            ExitCode.INTERNAL_ERROR.exit();
        } catch (InterruptedException e) {}
        return ExitCode.SUCCESS;
    }

    /**
     * Getter accessor for attribute 'database'.
     *
     * @return
     *       current value of 'database'
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Getter accessor for attribute 'cqlShOptionVersion'.
     *
     * @return
     *       current value of 'cqlShOptionVersion'
     */
    public boolean isCqlShOptionVersion() {
        return cqlShOptionVersion;
    }

    /**
     * Getter accessor for attribute 'cqlShOptionDebug'.
     *
     * @return
     *       current value of 'cqlShOptionDebug'
     */
    public boolean isCqlShOptionDebug() {
        return cqlShOptionDebug;
    }

    /**
     * Getter accessor for attribute 'cqlshOptionEncoding'.
     *
     * @return
     *       current value of 'cqlshOptionEncoding'
     */
    public String getCqlshOptionEncoding() {
        return cqlshOptionEncoding;
    }

    /**
     * Getter accessor for attribute 'cqlshOptionExecute'.
     *
     * @return
     *       current value of 'cqlshOptionExecute'
     */
    public String getCqlshOptionExecute() {
        return cqlshOptionExecute;
    }

    /**
     * Getter accessor for attribute 'cqlshOptionFile'.
     *
     * @return
     *       current value of 'cqlshOptionFile'
     */
    public String getCqlshOptionFile() {
        return cqlshOptionFile;
    }

    /**
     * Getter accessor for attribute 'cqlshOptionKeyspace'.
     *
     * @return
     *       current value of 'cqlshOptionKeyspace'
     */
    public String getCqlshOptionKeyspace() {
        return cqlshOptionKeyspace;
    }
    
}
