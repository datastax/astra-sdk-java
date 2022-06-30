package com.datastax.stargate.sdk.gql;

import java.util.List;

public class KeyspaceList {
    
    private List<KeyspaceResponse> keyspaces;

    /**
     * Getter accessor for attribute 'keyspaces'.
     *
     * @return
     *       current value of 'keyspaces'
     */
    public List<KeyspaceResponse> getKeyspaces() {
        return keyspaces;
    }

    /**
     * Setter accessor for attribute 'keyspaces'.
     * @param keyspaces
     * 		new value for 'keyspaces '
     */
    public void setKeyspaces(List<KeyspaceResponse> keyspaces) {
        this.keyspaces = keyspaces;
    }

    
    

}
