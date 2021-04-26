package com.datastax.astra.sdk.devops.res;

/**
 * Wrap Database Metrics.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseMetrics {
    
    private int writeRequestsTotalCount;
    
    private int readRequestsTotalCount;
    
    private int liveDataSizeBytes;
    
    private int errorsTotalCount;

    /**
     * Getter accessor for attribute 'writeRequestsTotalCount'.
     *
     * @return
     *       current value of 'writeRequestsTotalCount'
     */
    public int getWriteRequestsTotalCount() {
        return writeRequestsTotalCount;
    }

    /**
     * Setter accessor for attribute 'writeRequestsTotalCount'.
     * @param writeRequestsTotalCount
     * 		new value for 'writeRequestsTotalCount '
     */
    public void setWriteRequestsTotalCount(int writeRequestsTotalCount) {
        this.writeRequestsTotalCount = writeRequestsTotalCount;
    }

    /**
     * Getter accessor for attribute 'readRequestsTotalCount'.
     *
     * @return
     *       current value of 'readRequestsTotalCount'
     */
    public int getReadRequestsTotalCount() {
        return readRequestsTotalCount;
    }

    /**
     * Setter accessor for attribute 'readRequestsTotalCount'.
     * @param readRequestsTotalCount
     * 		new value for 'readRequestsTotalCount '
     */
    public void setReadRequestsTotalCount(int readRequestsTotalCount) {
        this.readRequestsTotalCount = readRequestsTotalCount;
    }

    /**
     * Getter accessor for attribute 'liveDataSizeBytes'.
     *
     * @return
     *       current value of 'liveDataSizeBytes'
     */
    public int getLiveDataSizeBytes() {
        return liveDataSizeBytes;
    }

    /**
     * Setter accessor for attribute 'liveDataSizeBytes'.
     * @param liveDataSizeBytes
     * 		new value for 'liveDataSizeBytes '
     */
    public void setLiveDataSizeBytes(int liveDataSizeBytes) {
        this.liveDataSizeBytes = liveDataSizeBytes;
    }

    /**
     * Getter accessor for attribute 'errorsTotalCount'.
     *
     * @return
     *       current value of 'errorsTotalCount'
     */
    public int getErrorsTotalCount() {
        return errorsTotalCount;
    }

    /**
     * Setter accessor for attribute 'errorsTotalCount'.
     * @param errorsTotalCount
     * 		new value for 'errorsTotalCount '
     */
    public void setErrorsTotalCount(int errorsTotalCount) {
        this.errorsTotalCount = errorsTotalCount;
    }
    

}
