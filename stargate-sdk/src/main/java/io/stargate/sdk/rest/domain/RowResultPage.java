package io.stargate.sdk.rest.domain;

import java.util.List;

import io.stargate.sdk.core.ResultPage;

/**
 * Result of API
 *
 * @author Cedrick LUNVEN (@clunven)s
 */
public class RowResultPage extends ResultPage<Row> {

    /**
     * Default constructor.
     */
    public RowResultPage() {
        super();
    }
    
    /**
     * Full constructor.
     */
    public RowResultPage(int pageSize, String pageState, List<Row> results) {
        super(pageSize,pageState,results);
    }
    
}
