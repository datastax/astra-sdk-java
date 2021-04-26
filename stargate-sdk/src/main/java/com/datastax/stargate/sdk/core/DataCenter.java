package com.datastax.stargate.sdk.core;

/**
 * Bean DataCenter.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DataCenter {

    private String name;
    
    private int replicas = 1;
    
    public DataCenter() {}
    
    public DataCenter(String name, int replicas) {
        super();
        this.name = name;
        this.replicas = replicas;
    }
    
    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }
    /**
     * Setter accessor for attribute 'name'.
     * @param name
     * 		new value for 'name '
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Getter accessor for attribute 'replicas'.
     *
     * @return
     *       current value of 'replicas'
     */
    public int getReplicas() {
        return replicas;
    }
    /**
     * Setter accessor for attribute 'replicas'.
     * @param replicas
     * 		new value for 'replicas '
     */
    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
