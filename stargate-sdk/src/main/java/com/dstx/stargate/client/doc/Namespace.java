package com.dstx.stargate.client.doc;

import java.util.List;

import com.dstx.stargate.client.rest.DataCenter;
import com.dstx.stargate.client.rest.Keyspace;

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
