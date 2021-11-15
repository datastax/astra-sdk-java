package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.stargate.sdk.audit.AnsiConsoleLogger;

public class FailOverCrossRegionAstra {

    public static final int NB_TRY_BEFORE_FAILOVER = 3;
    public static final int VACATIONS_SECONDS      = 2;
    
    public static void main(String[] args) throws Exception {
        try (AstraClient astraClient = astraClientEastFirst()) {
           SimpleStatement stmt = SimpleStatement.newInstance("SELECT data_center FROM system.local");
           stmt = stmt.setExecutionProfileName("us-east-1");
           System.out.println("DC (cql) " + astraClient
                   .cqlSession().execute(stmt).one().getString("data_center"));
           stmt = stmt.setExecutionProfileName("eu-central-1");
           System.out.println("DC (cql) " + astraClient
                   .cqlSession().execute(stmt).one().getString("data_center"));
           
           /*
           String regionName = "us-east-1";
           while(true) { 
               int idx = 0;
               while(idx++ < NB_TRY_BEFORE_FAILOVER) {
                   Thread.sleep(VACATIONS_SECONDS  *1000);
                   // Dummy invocation of api
                   for (DataCenter dc : astraClient
                           .apiStargateData() 
                           .keyspace("ks")
                           .find().get()
                           .getDatacenters()) {
                       System.out.println("DC (api)" + dc.getName());
                   }
                   
                   SimpleStatement stmt = SimpleStatement.newInstance("SELECT data_center FROM system.local");
                   stmt.setExecutionProfileName(regionName);
                   
                   System.out.println("DC (cql)" + astraClient
                           .cqlSession()
                           .execute(stmt)
                           .one().getString("data_center"));
                   
               }
               // switch region
               if (regionName.equals("us-east-1")) {
                   regionName = "eu-central-1";
               } else {
                   regionName = "us-east-1";
               }
               astraClient.useRegion(regionName);
               //astraClient.getStargateClient()
               //           .getStargateHttpClient()
               //           .failoverDatacenter();
           }*/
        }
    }
    
    public static AstraClient astraClientEastFirst() {
        return AstraClient.builder()
         .withToken("AstraCS:RjwrkBwzbcZZDyFZAvuhmCRJ:71cea89b9d3ea45a1c15675ab38f6cd861633bfc521467f525d2f57ced0e6b14")
         .withDatabaseId("290eb696-c9ed-48b2-ac22-350f71baaee7")
         .withDatabaseRegion("us-east-1")
         .withCqlKeyspace("ks")
         .addHttpObserver("log", new AnsiConsoleLogger())
         .build();
    }
   
}
