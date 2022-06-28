package com.datastax.astra.shell.cmd.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.databases.DatabaseClient;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseRegionServerless;
import com.datastax.astra.shell.ExitCode;
import com.datastax.astra.shell.OutputFormat;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.utils.LoggerShell;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.datastax.astra.shell.utils.ShellTable;

/**
 * Utility class for command `db`
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Db {

    /** Command constants. */
    public static final String DB                    = "db";
    
    /** Default region. **/
    public static final String DEFAULT_REGION        = "us-east-1";
    
    /** Default tier. **/
    public static final String DEFAULT_TIER          = "serverless";
    
    /** Allow Snake case. */
    public static final String KEYSPACE_NAME_PATTERN = "^[_a-z0-9]+$";
    
    
    /** column names. */
    public static final String COLUMN_ID                = "id";
    /** column names. */
    public static final String COLUMN_NAME              = "name";
    /** column names. */
    public static final String COLUMN_DEFAULT_REGION    = "default-region";
    /** column names. */
    public static final String COLUMN_STATUS            = "status";
    
    
    /**
     * Hide default constructor.
     */
    private Db() {}
    
    /**
     * Load the databaseClient by user input.
     * 
     * @param db
     *      database name or identifier
     * @return
     *      db id
     */
    public static Optional<DatabaseClient> getDatabaseClient(String db, OutputFormat fmt) {
        DatabasesClient dbsClient = ShellContext.getInstance().getApiDevopsDatabases();
        
        // Try with the id (fastest)
        DatabaseClient dbClient = dbsClient.database(db);
        if (dbClient.exist()) {
            return Optional.ofNullable(dbClient);
        }
        
        // Not found, try with the name
        List<Database> dbs = dbsClient
                .databasesNonTerminatedByName(db)
                .collect(Collectors.toList());
        
        // Multiple db with this name
        if (dbs.size() > 1) {
            LoggerShell.error("There are '" + dbs.size() + "' dbs with this name, try with id.");
            return Optional.empty();
        }
        
        // Db Found
        if (1 == dbs.size()) {
            return Optional.ofNullable(dbsClient.database(dbs.get(0).getId()));
        }
        
        LoggerShell.error("'" + db + "' database not found.");
        return Optional.empty();
    }
    
    /**
     * Mutualization of create db code (shell and cli)
     * 
     * @param cmd
     *      get all options of the command
     * @param databaseName
     *      db name
     * @param databaseRegion
     *      db region
     * @param defaultKeyspace
     *      db ks
     * @return
     */
    public static ExitCode createDb(BaseCommand cmd, String databaseName, String databaseRegion, String defaultKeyspace) {
        
        // Lookup for available serverless regions
        Map<String, DatabaseRegionServerless> regionMap = ShellContext.getInstance().getApiDevopsOrganizations()
                .regionsServerless()
                .collect(Collectors
                .toMap(DatabaseRegionServerless::getName, Function.identity()));
        if (cmd.isDebug()) {
            LoggerShell.trace("Available regions :" + regionMap);
        }
        
        // Validate region
        if (!regionMap.containsKey(databaseRegion)) {
            cmd.outputError(ExitCode.NOT_FOUND, "Database region '" + databaseRegion + "' has not been found");
            return ExitCode.NOT_FOUND;
        }
        
        // Validate keyspace
        if (!defaultKeyspace.matches(Db.KEYSPACE_NAME_PATTERN)) {
            cmd.outputError(ExitCode.INVALID_PARAMETER, "The keyspace name is not valid, please use snake_case: [a-z0-9_]");
            return ExitCode.INVALID_PARAMETER;
        }
        
        // We are ok to proceed
        String dbId = ShellContext.getInstance().getApiDevopsDatabases()
                .createDatabase(DatabaseCreationRequest.builder()
                        .name(databaseName)
                        .tier(Db.DEFAULT_TIER)
                        .cloudProvider(CloudProviderType.valueOf(regionMap
                                .get(databaseRegion)
                                .getCloudProvider()
                                .toUpperCase()))
                        .cloudRegion(databaseRegion)
                        .keyspace(defaultKeyspace)
                        .build());
        cmd.outputSuccess("Database [" + dbId + "] created.");
        return ExitCode.SUCCESS;
    }
    
    /**
     * List Databases.
     * 
     * @param cmd
     *      current command
     * @return
     *      returned code
     */
    public static ExitCode listDb(BaseCommand cmd) {
        ShellTable sht = new ShellTable();
        sht.addColumn(COLUMN_NAME,    20);
        sht.addColumn(COLUMN_ID,      37);
        sht.addColumn(COLUMN_DEFAULT_REGION, 20);
        sht.addColumn(COLUMN_STATUS,  15);
        ShellContext.getInstance()
           .getApiDevopsDatabases()
           .databasesNonTerminated()
           .forEach(db -> {
                Map <String, String> rf = new HashMap<>();
                rf.put(COLUMN_NAME,    db.getInfo().getName());
                rf.put(COLUMN_ID,      db.getId());
                rf.put(COLUMN_DEFAULT_REGION, db.getInfo().getRegion());
                rf.put(COLUMN_STATUS,  db.getStatus().name());
                sht.getCellValues().add(rf);
        });
        ShellPrinter.printShellTable(sht, cmd.getFormat());
        return ExitCode.SUCCESS;
    }
    
    
    
}
