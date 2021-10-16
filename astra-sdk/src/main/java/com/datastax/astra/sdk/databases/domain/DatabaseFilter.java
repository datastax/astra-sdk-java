/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.sdk.databases.domain;

import java.util.Optional;

/**
 * Create a filter.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseFilter {
    
    /** default limit */
    public static final int DEFAULT_LIMIT = 25;
    
    /** limit. */
    private final int limit;
    
    /** param offset. */
    private final String startingAfterDbId;
    
    /** should include non terminated.. */
    private final Include include;
    
    /** the providers to include. */
    private final CloudProviderType provider;
    
    /**
     * Default constructor.
     */
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
   
    /**
     * Full constructor.
     * 
     * @param limit
     *      limit to the number of db returned
     * @param i
     *      which db to inclue
     * @param p
     *      which cloud providers to provide
     * @param startingAfter
     *      when to start
     */
    public DatabaseFilter(int limit, Include i, CloudProviderType p, String startingAfter) {
        this.startingAfterDbId = startingAfter;
        this.limit             = limit;
        this.include           = i;
        this.provider          = p;
    }
  
    /**
     * Build the URL based on current parameters.
     *
     * @return
     *      target url to retrieved databases.
     */
    public String urlParams() {
        StringBuilder sbURL = new StringBuilder("?")
                .append("include=" + getInclude().name().toLowerCase())
                .append("&provider=" + getProvider().name().toLowerCase())
                .append("&limit=" + getLimit());
        if (getStartingAfterDbId().isPresent()) {
            sbURL.append("&starting_after=" + getStartingAfterDbId().get());
        }
        return sbURL.toString();
    }
    
    /**
     * Inclide Enum.
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static enum Include {
        
        /**
         * NON_TERMINATED
         */
        NON_TERMINATED,
        
        /**
         * ALL
         */
        ALL,
        
        /**
         * ACTIVE
         */
        ACTIVE,
        
        /**
         * PENDING
         */
        PENDING,
       
        /**
         * PREPARING
         */
        PREPARING,
        
        /**
         * PREPARED
         */
        PREPARED,
        
        /**
         * INITIALIZING
         */
        INITIALIZING,
        
        /**
         * PARKING
         */
        PARKING,
        
        /**
         * PARKED
         */
        PARKED,
        
        /**
         * UNPARKING
         */
        UNPARKING,
        
        /**
         * TERMINATING
         */
        TERMINATING,
        
        /**
         * TERMINATED
         */
        TERMINATED,
        
        /**
         * RESIZING
         */
        RESIZING,
        
        /**
         * ERROR
         */
        ERROR,
        
        /**
         * MAINTENANCE
         */
        MAINTENANCE,
        
        /**
         * HIBERNATING
         */
        HIBERNATING,
        
        /**
         * HIBERNATED
         */
        HIBERNATED;
    }
    
    /**
     * Helper to create a builder.
     *
     * @return
     *      an instance of the builder
     */
    public static DatabaseFilterBuilder builder() {
        return new DatabaseFilterBuilder();
    }
    
    /**
     * Builder.
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class DatabaseFilterBuilder {
        /** */
        private int limit = DEFAULT_LIMIT;
        /** */
        private String startingAfterDbId = null;
        /** */
        private CloudProviderType provider = CloudProviderType.ALL;
        /** */
        private Include include  = Include.NON_TERMINATED;
        
        /**
         * Default constructor.
         */
        public DatabaseFilterBuilder() {}
       
        /**
         * Define the limit.
         *
         * @param l
         *      the value for limit
         * @return
         *      this instance.
         */
        public DatabaseFilterBuilder limit(int l) {
            this.limit = l;
            return this;
        }
        
        /**
         * Define the dbId.
         *
         * @param dbId
         *      the value for dbId
         * @return
         *      this instance.
         */
        public DatabaseFilterBuilder startingAfterDbId(String dbId) {
            this.startingAfterDbId = dbId;
            return this;
        }

        /**
         * Define the CloudProviderType.
         *
         * @param p
         *      the value for CloudProviderType
         * @return
         *      this instance.
         */
        public DatabaseFilterBuilder provider(CloudProviderType p) {
            this.provider = p;
            return this;
        }

        /**
         * Define the Include.
         *
         * @param i
         *      the value for Include
         * @return
         *      this instance.
         */
        public DatabaseFilterBuilder include(Include i) {
            this.include = i;
            return this;
        }

        
        /**
         * Builld the immutable instance.
         *
         * @return
         *      an instance of Database filter
         */
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
    public CloudProviderType getProvider() {
        return provider;
    }


}
