package com.dstx.astra.sdk.document;

import java.util.List;

import com.dstx.astra.sdk.rest.DataCenter;
import com.dstx.astra.sdk.rest.Keyspace;

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
