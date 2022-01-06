package com.datastax.tutorial;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.core.Page;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.Document;
import com.datastax.stargate.sdk.doc.DocumentMapper;
import com.datastax.stargate.sdk.doc.domain.PageableQuery;
import com.datastax.stargate.sdk.utils.JsonUtils;


public class DocumentApiRaw {
    
    public static final String ASTRA_DB_TOKEN    = "AstraCS:gdZaqzmFZszaBTOlLgeecuPs:edd25600df1c01506f5388340f138f277cece2c93cb70f4b5fa386490daa5d44";
    public static final String ASTRA_DB_ID       = "3ed83de7-d97f-4fb6-bf9f-82e9f7eafa23";
    public static final String ASTRA_DB_REGION   = "eu-central-1";
    public static final String ASTRA_DB_KEYSPACE = "feeds_reader";
    
    public static void main(String[] args) {
       
        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_TOKEN)
                .withDatabaseId(ASTRA_DB_ID)
                .withDatabaseRegion(ASTRA_DB_REGION)
                .withCqlKeyspace(ASTRA_DB_KEYSPACE)
                .build()) {
            
            CollectionClient cp = astraClient.apiStargateDocument()
                    .namespace(ASTRA_DB_KEYSPACE)
                    .collection("person");
            
            PageableQuery query = PageableQuery.builder()
                    .selectAll()
                    .where("firstName").isEqualsTo("John")
                    .and("lastName").isEqualsTo("Connor")
                    .pageSize(3)
                    .build();
            
            Page<Document<String>> pageOfRecords = cp.findPage(query);
            pageOfRecords.getResults().stream()
                         .map(Document::getDocument)
                         .forEach(System.out::println);
            
            Page<Document<Person>> pageOfPerson = cp.findPage(query, new PersonMapper());
            pageOfPerson.getResults().stream()
                        .forEach(p -> System.out.println(p.getDocument().getEmail()));
            
            Page<Document<Person>> pageOfPerson2 = cp.findPage(query, Person.class);
            pageOfPerson2.getResults().stream()
                        .forEach(p -> System.out.println(p.getDocument().getEmail()));
        }
    }
    
    /**
     * Map as Bean.
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class PersonMapper implements DocumentMapper<Person> {

        /** {@inheritDoc} */
        @Override
        public Person map(String record) {
            return JsonUtils.unmarshallBean(record, Person.class);
        }
        
    }
    
   
    
}
