package com.dstx.stargate.client.doc;

/**
 * Wrapper for an document retrieved from ASTRA caring a unique identifier.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <BEAN>
 *      target bean to store in ASTRA / STARGATE
 */
public class AstraDocument<BEAN> {
    
    /** Unique identifier. */
    private String documentId;
    
    /** Marshalled Object. */
    private BEAN document;
    
    /**
     * Default constructor
     */
    public AstraDocument() {}
    
    /**
     * Constructor with Params
     */
    public AstraDocument(String docId, BEAN doc) {
        this.documentId = docId;
        this.document = doc;
    }

    /**
     * Getter accessor for attribute 'documentId'.
     *
     * @return
     *       current value of 'documentId'
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Setter accessor for attribute 'documentId'.
     * @param documentId
     * 		new value for 'documentId '
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Getter accessor for attribute 'document'.
     *
     * @return
     *       current value of 'document'
     */
    public BEAN getDocument() {
        return document;
    }

    /**
     * Setter accessor for attribute 'document'.
     * @param document
     * 		new value for 'document '
     */
    public void setDocument(BEAN document) {
        this.document = document;
    }
    

}
