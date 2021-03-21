package io.stargate.sdk.doc;

import java.util.List;

import io.stargate.sdk.rest.DataCenter;
import io.stargate.sdk.rest.Keyspace;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class Namespace extends Keyspace{
    
    public Namespace() {}
            
    public Namespace(String name, List<DataCenter> datacenters) {
        super(name,datacenters);
    }
    
}
