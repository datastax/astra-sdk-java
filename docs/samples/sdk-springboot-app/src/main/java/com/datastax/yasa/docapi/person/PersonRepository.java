package com.datastax.yasa.docapi.person;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.doc.Document;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;

/**
 * Work like Spring Data for Collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Repository
public class PersonRepository extends StargateDocumentRepository<Person> implements InitializingBean {
    
    /**
     * Constructor from {@link AstraClient}.
     * 
     * @param astraClient
     *      client for Astra
     */
    public PersonRepository(AstraClient astraClient) {
        super(astraClient.apiStargateDocument().namespace(
                astraClient.cqlSession().getKeyspace().get().toString())
                , Person.class);
    }
    
    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet() throws Exception {
        Address a1 = new Address(20, "Champ Elysees", "PARIS", 75008);
        insert(new Person("Cedrick", "Lunven", "lala@hotmail.com", Arrays.asList(a1)));
        insert(new Person("John", "Connor", "jc@hotmail.com", Arrays.asList(a1)));
        insert(new Person("RAM", "RAM", "jc@hotmail.com", Arrays.asList(a1)));
    }
    
    /**
     * Sample of custom code
     * @param lastName
     * @return
     */
    public Stream<Document<Person>> findPersonByLastName(String lastName) {
        return search(SearchDocumentQuery.builder()
                .where("lastName").isEqualsTo(lastName)
                .build());
    }

   
    
}
