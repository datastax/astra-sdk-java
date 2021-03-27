package io.stargate.sdk.doc;

import java.util.List;

import io.stargate.sdk.rest.DataCenter;
import io.stargate.sdk.rest.Keyspace;

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
