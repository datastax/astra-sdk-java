package io.stargate.sdk.doc.domain;

import java.util.List;

import io.stargate.sdk.core.DataCenter;
import io.stargate.sdk.rest.domain.Keyspace;

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
