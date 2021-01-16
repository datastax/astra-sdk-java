package org.datastax.astra.doc;


import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Working document with StargateApi.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <T>
 *      current object
 */
@JsonIgnoreProperties
public class Document<T> implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -9031206707641391885L;
    
    /** Provide the collection Name . */
    @JsonIgnore
    protected String collectionName;
    
    @JsonProperty("documentId")
    protected String documentId = null;
    
    final Class<T> typeParameterClass;
    
    
    @JsonProperty("data")
    protected T data;
   
    
    public Document(T val, Class<T> typeParameterClass) {
        this.data = val;
        this.typeParameterClass = typeParameterClass;
    }
    
    public Document(T val,  Class<T> typeParameterClass, String docid) {
        this(val, typeParameterClass);
        this.documentId = docid;
    }
    
    @SuppressWarnings("unchecked")
    public String getGenericName() {
        return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]).getTypeName();
    }
    
    /**
     * Getter accessor for attribute 'collectionName'.
     *
     * @return
     *       current value of 'collectionName'
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * Setter accessor for attribute 'collectionName'.
     * @param collectionName
     *      new value for 'collectionName '
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * Getter accessor for attribute 'documentId'.
     *
     * @return
     *       current value of 'documentId'
     */
    public Optional<String> getDocumentId() {
        return Optional.ofNullable(documentId);
    }

    /**
     * Setter accessor for attribute 'documentId'.
     * @param documentId
     *      new value for 'documentId '
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Getter accessor for attribute 'data'.
     *
     * @return
     *       current value of 'data'
     */
    public T getData() {
        return data;
    }

    /**
     * Setter accessor for attribute 'data'.
     * @param data
     *      new value for 'data '
     */
    public void setData(T data) {
        this.data = data;
    }
}