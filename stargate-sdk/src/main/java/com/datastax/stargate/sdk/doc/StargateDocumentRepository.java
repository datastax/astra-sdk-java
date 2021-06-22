package com.datastax.stargate.sdk.doc;

import java.util.Optional;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.doc.domain.DocumentResultPage;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;

/**
 * Super class to help you working with the document API.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DOC>
 *      current document bean.
 */
public class StargateDocumentRepository <DOC> extends AbstractDocumentSupport<DOC>{
    
    /** Reference to underlying collection client. **/
    private final CollectionClient collectionClient;
    
    /**
     * Constructor from {@link StargateClient}.
     *
     * @param stargateClient
     *      reference to the StargateClient
     * @param namespace
     *      working namespace identifier
     */
    public StargateDocumentRepository(StargateClient stargateClient, String namespace) {
        this.collectionClient = new CollectionClient(
                stargateClient.apiDocument(), 
                stargateClient.apiDocument().namespace(namespace),
                getCollectionIdFromBean());
    } 
    
    /**
     * Create a new document an generating identifier.
     *
     * @param p
     *      working document
     * @return
     *      an unique identifier for the document
     */
    public String create(DOC p) {
        return collectionClient.create(p);
    }
    
    /**
     * Find a person from ids unique identifier.
     *
     * @param docId
     *      document Id
     * @return
     */
    public Optional<DOC> find(String docId) {
        return collectionClient.document(docId).find(getGenericClass());
    }
    
    /**
     * Find a person from ids unique identifier.
     *
     * @param docId
     *      document Id
     * @return
     */
    public boolean exist(String docId) {
        return find(docId).isPresent();
    }
    
    /**
     * Search document with attributes.
     * 
     * @param query
     *      current query
     * @return
     *      result page
     */
    public DocumentResultPage<DOC> search(SearchDocumentQuery query) {
        return collectionClient.search(query, getGenericClass());
    }
    
    /**
     * Retrieve all documents from the collection.
     * 
     * @return
     *      every document of the collection
     */
    public Stream<ApiDocument<DOC>> findAll() {
        return collectionClient.findAll(getGenericClass());
    }
    
    
    
}  
