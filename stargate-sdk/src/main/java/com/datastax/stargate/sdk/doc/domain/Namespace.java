package com.datastax.stargate.sdk.doc.domain;

import java.util.List;

import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.rest.domain.Keyspace;

/**
 * Object abstraction for document api.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Namespace extends Keyspace{
    
    public Namespace() {}
            
    public Namespace(String name, List<DataCenter> datacenters) {
        super(name,datacenters);
    }
    
    public Namespace(String name, int replicas) {
        super(name, replicas);
    }
    
}
