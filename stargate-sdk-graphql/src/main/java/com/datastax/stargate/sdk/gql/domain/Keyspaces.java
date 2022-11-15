package com.datastax.stargate.sdk.gql.domain;

import com.datastax.stargate.graphql.types.Keyspace;

import java.util.List;

/**
 * List of keyspaces.
 * @author Cedrick LUNVEN (@clunven)
 */
public class Keyspaces {
    
    private List<Keyspace> keyspaces;

    /**
     * Getter accessor for attribute 'keyspaces'.
     *
     * @return
     *       current value of 'keyspaces'
     */
    public List<Keyspace> getKeyspaces() {
        return keyspaces;
    }

    /**
     * Setter accessor for attribute 'keyspaces'.
     * @param keyspaces
     * 		new value for 'keyspaces '
     */
    public void setKeyspaces(List<Keyspace> keyspaces) {
        this.keyspaces = keyspaces;
    }

    
    

}
