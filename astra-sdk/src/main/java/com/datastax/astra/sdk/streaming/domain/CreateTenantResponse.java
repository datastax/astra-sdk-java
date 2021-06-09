package com.datastax.astra.sdk.streaming.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
@JsonIgnoreProperties
public class CreateTenantResponse extends Tenant {
    
    private String namespace;
    private String topic;
    
    /**
     * Getter accessor for attribute 'namespace'.
     *
     * @return
     *       current value of 'namespace'
     */
    public String getNamespace() {
        return namespace;
    }
    /**
     * Setter accessor for attribute 'namespace'.
     * @param namespace
     * 		new value for 'namespace '
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    /**
     * Getter accessor for attribute 'topic'.
     *
     * @return
     *       current value of 'topic'
     */
    public String getTopic() {
        return topic;
    }
    /**
     * Setter accessor for attribute 'topic'.
     * @param topic
     * 		new value for 'topic '
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
}
