package org.datastax.astra.sdk.domain;

import java.util.List;

/**
 * Response Limit.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantLimit {

    private int namespace_limit;
    
    private int topic_per_namespace_limit;
    
    private TenantLimitUsage usage;
    
    /**
     * Custom ussage.
     * 
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class TenantLimitUsage {
        
        private String namespace;
        
        private List<String> topics;
        
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
         * Getter accessor for attribute 'topics'.
         *
         * @return
         *       current value of 'topics'
         */
        public List<String> getTopics() {
            return topics;
        }
        
        /**
         * Setter accessor for attribute 'topics'.
         * @param topics
         * 		new value for 'topics '
         */
        public void setTopics(List<String> topics) {
            this.topics = topics;
        }
    }

    /**
     * Getter accessor for attribute 'namespace_limit'.
     *
     * @return
     *       current value of 'namespace_limit'
     */
    public int getNamespace_limit() {
        return namespace_limit;
    }

    /**
     * Setter accessor for attribute 'namespace_limit'.
     * @param namespace_limit
     * 		new value for 'namespace_limit '
     */
    public void setNamespace_limit(int namespace_limit) {
        this.namespace_limit = namespace_limit;
    }

    /**
     * Getter accessor for attribute 'topic_per_namespace_limit'.
     *
     * @return
     *       current value of 'topic_per_namespace_limit'
     */
    public int getTopic_per_namespace_limit() {
        return topic_per_namespace_limit;
    }

    /**
     * Setter accessor for attribute 'topic_per_namespace_limit'.
     * @param topic_per_namespace_limit
     * 		new value for 'topic_per_namespace_limit '
     */
    public void setTopic_per_namespace_limit(int topic_per_namespace_limit) {
        this.topic_per_namespace_limit = topic_per_namespace_limit;
    }

    /**
     * Getter accessor for attribute 'usage'.
     *
     * @return
     *       current value of 'usage'
     */
    public TenantLimitUsage getUsage() {
        return usage;
    }

    /**
     * Setter accessor for attribute 'usage'.
     * @param usage
     * 		new value for 'usage '
     */
    public void setUsage(TenantLimitUsage usage) {
        this.usage = usage;
    }
}
