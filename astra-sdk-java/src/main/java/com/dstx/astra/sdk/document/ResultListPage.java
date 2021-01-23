package com.dstx.astra.sdk.document;

import java.util.List;
import java.util.Optional;

/**
 * Hold results for paging
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DOC>
 *      document type
 */
public class ResultListPage< DOC > {
 
    /** size of page asked. */
    private final int pageSize;
    
    /** Of present there is a next page. */
    private final String pageState;
    
    /** list of results matchin the request. */
    private final List< AstraDocument<DOC>> results;
    
    public ResultListPage() {
        this.pageSize  = 0;
        this.pageState = null;
        this.results   = null;
    }
    /**
     * Default constructor.
     */
    public ResultListPage(int pageSize, String pageState, List<AstraDocument<DOC>> results) {
        this.pageSize  = pageSize;
        this.pageState = pageState;
        this.results   = results;
    }
 
    /**
     * Getter accessor for attribute 'pageSize'.
     *
     * @return
     *       current value of 'pageSize'
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Getter accessor for attribute 'pageState'.
     *
     * @return
     *       current value of 'pageState'
     */
    public Optional<String> getPageState() {
        return Optional.ofNullable(pageState);
    }

    /**
     * Getter accessor for attribute 'results'.
     *
     * @return
     *       current value of 'results'
     */
    public List<AstraDocument<DOC>> getResults() {
        return results;
    }
}
