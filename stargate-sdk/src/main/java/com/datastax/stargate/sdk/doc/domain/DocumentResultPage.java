package com.datastax.stargate.sdk.doc.domain;

import java.util.List;

import com.datastax.stargate.sdk.core.ResultPage;
import com.datastax.stargate.sdk.doc.ApiDocument;

/**
 * Hold results for paging
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DOC>
 *      document type
 */
public class DocumentResultPage< DOC > extends ResultPage<ApiDocument<DOC>> {
    
    /**
     * Default constructor.
     */
    public DocumentResultPage() {
        super();
    }
    
    
    /**
     * Full constructor.
     */
    public DocumentResultPage(int pageSize, String pageState, List<ApiDocument<DOC>> results) {
        super(pageSize,pageState,results);
    }
    
}
