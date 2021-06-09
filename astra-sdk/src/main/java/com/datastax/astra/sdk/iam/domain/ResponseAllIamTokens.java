package com.datastax.astra.sdk.iam.domain;

import java.util.List;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class ResponseAllIamTokens {
    
    private List<IamToken> clients;

    /**
     * Getter accessor for attribute 'clients'.
     *
     * @return
     *       current value of 'clients'
     */
    public List<IamToken> getClients() {
        return clients;
    }

    /**
     * Setter accessor for attribute 'clients'.
     * @param clients
     * 		new value for 'clients '
     */
    public void setClients(List<IamToken> clients) {
        this.clients = clients;
    }

}
