package com.datastax.astra.devops.org.domain;

import java.util.List;

/**
 * Represents response of Iam token list.
 */
public class ResponseAllIamTokens {

    /**
     * client lists.
     */
    private List<IamToken> clients;

    /**
     * Default constructor.
     */
    public ResponseAllIamTokens() {}

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
