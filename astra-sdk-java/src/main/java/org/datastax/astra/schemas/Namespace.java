package org.datastax.astra.schemas;

import java.util.List;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class Namespace extends Keyspace{
    
    public Namespace(String name, List<DataCenter> datacenters) {
        super(name,datacenters);
    }
    
}
