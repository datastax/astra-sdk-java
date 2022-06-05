package com.datastax.astra.shell.cmd.db;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseRegionServerless;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.astra.shell.jansi.Out;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Create a new Database
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "db", description = "Create a new database")
public class CreateDatabaseCommand extends BaseCommand<CreateDatabaseCommand> {

    @Required
    @Option(name = { "-n", "--name" }, 
            title = "DbName", 
            arity = 1, 
            description = "Name for the Database (not unique)")
    private String databaseName;
    
    @Required
    @Option(name = { "-r", "--region" }, 
            title = "DbRegion", 
            arity = 1, 
            description = "Cloud provider region to provision")
    private String databaseRegion;
    
    @Required
    @Option(name = { "-ks", "--keyspace" }, 
            title = "keyspace", 
            arity = 1, 
            description = "Default keyspace created with the Db")
    private String defaultKeyspace;

    /** {@inheritDoc} */
    @Override
    public void execute() {
       // List regions
       Map<String, DatabaseRegionServerless> regionMap = getApiDevopsOrg()
               .regionsServerless()
               .collect(Collectors
               .toMap(DatabaseRegionServerless::getName, Function.identity()));
       
       if (!regionMap.containsKey(databaseRegion)) {
           Out.error("The database region has not been found");
           Out.info("Available regions are: " + regionMap.keySet());
       } else {
           String dbId = getApiDevopsDb().createDatabase(DatabaseCreationRequest.builder()
                .name(databaseName)
                .tier("serverless")
                .cloudProvider(CloudProviderType.valueOf(regionMap
                        .get(databaseRegion)
                        .getCloudProvider()
                        .toUpperCase()))
                .cloudRegion(databaseRegion)
                .keyspace(defaultKeyspace)
                .build());
           Out.success("Creating database... " + dbId);
           Out.info("To get status: astra show db < dbName | dbId >");
       }
    }
    
    
    
}
