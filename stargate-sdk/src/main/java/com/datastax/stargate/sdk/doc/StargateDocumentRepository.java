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
public class StargateDocumentRepository <DOC> {
    
    /** Reference to underlying collection client. **/
    private final CollectionClient collectionClient;
    
    /** Keep ref to the generic. */
    private final Class<DOC> docClass;
    
    /**
     * Default constructor.
     *
     * @param col
     *      collection client parent
     * @param clazz
     *      working bean class
     */
    public StargateDocumentRepository(CollectionClient col, Class<DOC> clazz) {
        this.collectionClient = col;
        this.docClass        = clazz;
    }
    
    /**
     * Constructor from {@link StargateClient}.
     *
     * @param nc
     *      reference to the StargateClient
     * @param clazz
     *      working namespace identifier
     */
    public StargateDocumentRepository(NamespaceClient nc, Class<DOC> clazz) {
        this.docClass = clazz;
        this.collectionClient = new CollectionClient(nc.stargateHttpClient, nc, getCollectionName(clazz));
    } 
    
    /**
     * Count items in the collection.
     * 
     * @return
     *      number of records.
     */
    public int count() {
        return collectionClient.count();
    }
    
    /**
     * Delete a document from its iid.
     *
     * @param docId
     *          document identifier
     */
    public void delete(String docId) {
        collectionClient.document(docId).delete();;
    }
    
    /**
     * Check existence of a document from its id.
     * 
     * @param docId
     *      document identifier
     * @return
     *      existence status
     */
    public boolean exists(String docId) {
        return collectionClient.document(docId).exist();
    }
    
    /**
     * Create a new document an generating identifier.
     *
     * @param p
     *      working document
     * @return
     *      an unique identifier for the document
     */
    public String insert(DOC p) {
        return collectionClient.create(p);
    }
    
    /**
     * Create if not exist with defined ID.
     *
     * @param docId
     *      expected document id
     * @param doc
     *      document
     */
    public void insert(String docId, DOC doc) {
        collectionClient.document(docId).upsert(doc);
    }
    
    /**
     * Create if not exist with defined ID.
     *
     * @param docId
     *      expected document id
     * @param doc
     *      document
     */
    public void save(String docId, DOC doc) {
        collectionClient.document(docId).upsert(doc);
    }
    
    /**
     * Evaluation on which collection we are working.
     *
     * @return
     *      collection identifier
     */
    public String getCollectionName() {
        return collectionClient.getCollectionName();
    }
    
    /**
     * Find a person from ids unique identifier.
     *
     * @param docId
     *      document Id
     * @return
     *      the object only if present
     */
    public Optional<DOC> find(String docId) {
        return collectionClient.document(docId).find(docClass);
    }
   
    /**
     * Search document with attributes.
     * 
     * @param query
     *      current query
     * @return
     *      result page
     */
    public DocumentResultPage<DOC> findPage(SearchDocumentQuery query) {
        return collectionClient.findPage(query, docClass);
    }
    
    /**
     * Retrieve all documents from the collection.
     *
     * @param query
     *      search query
     * @return
     *      every document of the collection
     */
    public Stream<ApiDocument<DOC>> findAll(SearchDocumentQuery query) {
        return collectionClient.findAll(query, docClass);
    }
    /**
     * Retrieve all documents from the collection.
     * 
     * @return
     *      every document of the collection
     */
    public Stream<ApiDocument<DOC>> findAll() {
        return collectionClient.findAll(docClass);
    }
    
    public DocumentResultPage<DOC> findFirstPage() {
        return collectionClient.findFirstPage(docClass);
    }
    
    public DocumentResultPage<DOC> findFirstPage(int pageSize){
        return collectionClient.findFirstPage(docClass, pageSize);
    }
    
    public DocumentResultPage<DOC> findPage(int pageSize, String pageingState){
        return collectionClient.findPage(docClass, pageSize, pageingState);
    }
    
    /**
     * Read Collection Name.
     * 
     * @param myClass
     *      my current class
     * @return
     *      name of the collection
     */
    private String getCollectionName(Class<DOC> myClass) {
        Collection ann = myClass.getAnnotation(Collection.class);
        if (null != ann && ann.value() !=null && !ann.value().equals("")) {
            return ann.value();
        } else {
            return myClass.getSimpleName().toLowerCase();
        }
    }
    
}  
