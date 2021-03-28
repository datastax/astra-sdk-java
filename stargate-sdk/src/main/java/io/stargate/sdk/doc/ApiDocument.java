package io.stargate.sdk.doc;

/**
 * Wrapper for an document retrieved from ASTRA caring a unique identifier.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <BEAN>
 *      target bean to store in ASTRA / STARGATE
 */
public class ApiDocument<BEAN> {
    
    /** Unique identifier. */
    private final String documentId;
    
    /** Marshalled Object. */
    private final BEAN document;
    
    /**
     * Constructor with Params
     */
    public ApiDocument(String docId, BEAN doc) {
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
     * Getter accessor for attribute 'document'.
     *
     * @return
     *       current value of 'document'
     */
    public BEAN getDocument() {
        return document;
    }

}
