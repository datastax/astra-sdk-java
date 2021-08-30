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
     *Deafult constructor
     * @param col
     * @param clazz
     */
    public StargateDocumentRepository(CollectionClient col, Class<DOC> clazz) {
        this.collectionClient = col;
        this.docClass        = clazz;
    }
    
    /**
     * Constructor from {@link StargateClient}.
     *
     * @param stargateClient
     *      reference to the StargateClient
     * @param namespace
     *      working namespace identifier
     */
    public StargateDocumentRepository(NamespaceClient nc, Class<DOC> clazz) {
        this.docClass = clazz;
        this.collectionClient = new CollectionClient(nc, getCollectionName(clazz));
    } 
    
    /*
    count, delete, deleteAll, deleteAll, deleteAllById, deleteById, existsById, findAllById, findById, save
    count, exists, findAll, findOne
    findAll
    FindAll(Sort_)inser<Iterablt
    insert
    saveALL<Iterable?
    */
     
    
    
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
     * Find a person from ids unique identifier.
     *
     * @param docId
     *      document Id
     * @return
     */
    public Optional<DOC> find(String docId) {
        return collectionClient.document(docId).find(docClass);
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
    public DocumentResultPage<DOC> searchPageable(SearchDocumentQuery query) {
        return collectionClient.searchPageable(query, docClass);
    }
    
    /**
     * Retrieve all documents from the collection.
     * 
     * @return
     *      every document of the collection
     */
    public Stream<ApiDocument<DOC>> search(SearchDocumentQuery query) {
        return collectionClient.search(query, docClass);
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
    
    public DocumentResultPage<DOC> findAllPageable() {
        return collectionClient.findAllPageable(docClass);
    }
    
    public DocumentResultPage<DOC> findAllPageable(int pageSize){
        return collectionClient.findAllPageable(docClass, pageSize);
    }
    
    public DocumentResultPage<DOC> findAllPageable(int pageSize, String pageingState){
        return collectionClient.findAllPageable(docClass, pageSize, pageingState);
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
