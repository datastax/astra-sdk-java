package com.dstx.astra.sdk.devops;

import java.util.Optional;

public class DatabaseFilter {
    
    public static final int DEFAULT_LIMIT = 25;
    
    private final int limit;
    
    private final String startingAfterDbId;
    
    private final Include include;
    
    private final CloudProvider provider;
    
    public DatabaseFilter() {
        DatabaseFilter f = DatabaseFilter.builder().build();
        if (f.getStartingAfterDbId().isPresent()) {
            this.startingAfterDbId = f.getStartingAfterDbId().get();
        } else {
            this.startingAfterDbId = null;
        }
        this.limit             = f.getLimit();
        this.include           = f.getInclude();
        this.provider          = f.getProvider();
    }
    
    public DatabaseFilter(int limit, Include i, CloudProvider p, String startingAfter) {
        this.startingAfterDbId = startingAfter;
        this.limit             = limit;
        this.include           = i;
        this.provider          = p;
    }
    
    public static enum Include {
        NON_TERMINATED,ALL,ACTIVE,PENDING,
        PREPARING,PREPARED,INITIALIZING,
        PARKING,PARKED,UNPARKING,
        TERMINATING,TERMINATED,
        RESIZING,ERROR,MAINTENANCE;
    }
    
    
    public static DatabaseFilterBuilder builder() {
        return new DatabaseFilterBuilder();
    }
    
    public static class DatabaseFilterBuilder {
        private int limit = DEFAULT_LIMIT;
        private String startingAfterDbId = null;
        private CloudProvider provider = CloudProvider.ALL;
        private Include include  = Include.NON_TERMINATED;
        
        public DatabaseFilterBuilder() {}
        
        public DatabaseFilterBuilder limit(int l) {
            this.limit = l;
            return this;
        }
        public DatabaseFilterBuilder startingAfterDbId(String dbId) {
            this.startingAfterDbId = dbId;
            return this;
        }
        public DatabaseFilterBuilder provider(CloudProvider p) {
            this.provider = p;
            return this;
        }
        public DatabaseFilterBuilder include(Include i) {
            this.include = i;
            return this;
        }
        public DatabaseFilter build() {
            return new DatabaseFilter(limit, include, provider, startingAfterDbId);
        }
        
    }

    /**
     * Getter accessor for attribute 'limit'.
     *
     * @return
     *       current value of 'limit'
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Getter accessor for attribute 'startingAfterDbId'.
     *
     * @return
     *       current value of 'startingAfterDbId'
     */
    public Optional<String> getStartingAfterDbId() {
        return Optional.ofNullable(startingAfterDbId);
    }
    
    /**
     * Getter accessor for attribute 'include'.
     *
     * @return
     *       current value of 'include'
     */
    public Include getInclude() {
        return include;
    }

    /**
     * Getter accessor for attribute 'provider'.
     *
     * @return
     *       current value of 'provider'
     */
    public CloudProvider getProvider() {
        return provider;
    }


}
