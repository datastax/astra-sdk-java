package io.stargate.sdk.rest;

import java.util.List;

/**
 * Represent a keyspace definition with its relevant MetaData.
 */
public class Keyspace {
    
    /** Unique identifier for the keyspace. */
    protected String name;
    
    /** This property is used for local deployments. (SimpleStrategy) */
    protected Integer replicas;
    
    /** This property is used for distributed deployment (NetworkTopologyStrategy). */
    protected List<DataCenter> datacenters;

    public Keyspace() {}
            
    public Keyspace(String name, List<DataCenter> datacenters) {
        super();
        this.name = name;
        this.datacenters = datacenters;
    }
    
    public Keyspace(String name, int replicas) {
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
     * Getter accessor for attribute 'datacenters'.
     *
     * @return
     *       current value of 'datacenters'
     */
    public List<DataCenter> getDatacenters() {
        return datacenters;
    }

    /**
     * Setter accessor for attribute 'datacenters'.
     * @param datacenters
     * 		new value for 'datacenters '
     */
    public void setDatacenters(List<DataCenter> datacenters) {
        this.datacenters = datacenters;
    }

    /**
     * Getter accessor for attribute 'replicas'.
     *
     * @return
     *       current value of 'replicas'
     */
    public Integer getReplicas() {
        return replicas;
    }

    /**
     * Setter accessor for attribute 'replicas'.
     * @param replicas
     * 		new value for 'replicas '
     */
    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }
    

}
