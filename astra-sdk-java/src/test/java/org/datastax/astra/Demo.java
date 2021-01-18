package org.datastax.astra;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.datastax.astra.schemas.DataCenter;


public class Demo {
    
    public void demo() {
        
       AstraClient astra = AstraClient.builder()
                .astraDatabaseId( "e92195f2-159f-492e-9777-3dadda3ff1a3")
                .astraDatabaseRegion("europe-west1")
                .username("todouser")
                .password("todoPassword1")
                .tokenTtl(Duration.ofSeconds(300))
                .build();
       
       astra.connect();
       
       // Work with REST API
       astra.keyspaces();
       astra.keyspaceNames();

       // Work with document PAI
       astra.namespaceNames();
       astra.namespaces();

       // CRUD on namespace
       astra.namespace("namespace1").exist();
       astra.namespace("namespace1").create(Arrays.asList(new DataCenter("dc-1", 1)));
       astra.namespace("namespace1").delete();
       
       // Work on collections
       astra.namespace("namespace1").collectionNames();
       astra.namespace("namespace1").collection("person").exist();
       astra.namespace("namespace1").collection("person").delete();
       astra.namespace("namespace1").collection("person").create();
       
       // Work on a single Document
       String docId = 
       astra.namespace("namespace1").collection("person").save(new Person("Cedrick", "LUNVEN"));
       astra.namespace("namespace1").collection("person").saveAsync(new Person("Cedrick", "LUNVEN"));
               
       Optional<Person> person = 
       astra.namespace("namespace1").collection("person").document(docId).find(Person.class);
       
       person.get().setLastname("LULU");
       astra.namespace("namespace1").collection("person").document(docId).save(person.get());
       astra.namespace("namespace1").collection("person").document(docId).delete();
       
       // FindAll with Paging
       //int pageSize = 5;
       //astra.namespace("namespace1").collection("person").findAll(pageSize, "", Person.class);
       
       // Search
       //astra.namespace("namespace1").collection("person").search("lastName", DocOperator.EQUALS, "LULU");
       //astra.namespace("namespace1").collection("person").search("age", DocOperator.GREATER_THAN, 30);
       
       
       
       
    }
            

}
