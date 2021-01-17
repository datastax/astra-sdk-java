package org.datastax.astra.schemas;

import java.util.ArrayList;
import java.util.List;

public class Keyspace {
    
    String name;
    
    List<DataCenter> datacenters = new ArrayList<>();

    public Keyspace() {}
    
    public Keyspace(String name, List<DataCenter> datacenters) {
        super();
        this.name = name;
        this.datacenters = datacenters;
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
    

}
