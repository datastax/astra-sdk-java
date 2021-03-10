package com.dstx.astra.sdk.devops.res;

/**
 * Represents Storage information for the db.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseStorage {
    
    private int nodeCount;
    
    private int replicationFactor;
    
    private int totalStorage;
    
    private int usedStorage;

    /**
     * Getter accessor for attribute 'nodeCount'.
     *
     * @return
     *       current value of 'nodeCount'
     */
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Setter accessor for attribute 'nodeCount'.
     * @param nodeCount
     * 		new value for 'nodeCount '
     */
    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    /**
     * Getter accessor for attribute 'replicationFactor'.
     *
     * @return
     *       current value of 'replicationFactor'
     */
    public int getReplicationFactor() {
        return replicationFactor;
    }

    /**
     * Setter accessor for attribute 'replicationFactor'.
     * @param replicationFactor
     * 		new value for 'replicationFactor '
     */
    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    /**
     * Getter accessor for attribute 'totalStorage'.
     *
     * @return
     *       current value of 'totalStorage'
     */
    public int getTotalStorage() {
        return totalStorage;
    }

    /**
     * Setter accessor for attribute 'totalStorage'.
     * @param totalStorage
     * 		new value for 'totalStorage '
     */
    public void setTotalStorage(int totalStorage) {
        this.totalStorage = totalStorage;
    }

    /**
     * Getter accessor for attribute 'usedStorage'.
     *
     * @return
     *       current value of 'usedStorage'
     */
    public int getUsedStorage() {
        return usedStorage;
    }

    /**
     * Setter accessor for attribute 'usedStorage'.
     * @param usedStorage
     * 		new value for 'usedStorage '
     */
    public void setUsedStorage(int usedStorage) {
        this.usedStorage = usedStorage;
    }
    

}
