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
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseFilter {
    
    /** */
    public static final int DEFAULT_LIMIT = 25;
    
    /** */
    private final int limit;
    
    /** */
    private final String startingAfterDbId;
    
    /** */
    private final Include include;
    
    /** */
    private final CloudProviderType provider;
    
    /** */
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
     * 
     */
    public DatabaseFilter(int limit, Include i, CloudProviderType p, String startingAfter) {
        this.startingAfterDbId = startingAfter;
        this.limit             = limit;
        this.include           = i;
        this.provider          = p;
    }
    
    /**
     * 
     */
    public String urlParams() {
        StringBuilder sbURL = new StringBuilder("/databases?")
                .append("include=" + getInclude().name().toLowerCase())
                .append("&provider=" + getProvider().name().toLowerCase())
                .append("&limit=" + getLimit());
        if (!getStartingAfterDbId().isEmpty()) {
            sbURL.append("&starting_after=" + getStartingAfterDbId().get());
        }
        return sbURL.toString();
    }
    
    /**
     * @author Cedrick LUNVEN (@clunven)
     */
    public static enum Include {
        NON_TERMINATED,ALL,ACTIVE,PENDING,
        PREPARING,PREPARED,INITIALIZING,
        PARKING,PARKED,UNPARKING,
        TERMINATING,TERMINATED,
        RESIZING,ERROR,MAINTENANCE;
    }
    
    /**
     * 
     */
    public static DatabaseFilterBuilder builder() {
        return new DatabaseFilterBuilder();
    }
    
    /** 
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
        
        public DatabaseFilterBuilder() {}
        
        /**
         * 
         */
        public DatabaseFilterBuilder limit(int l) {
            this.limit = l;
            return this;
        }

        /**
         * 
         */
        public DatabaseFilterBuilder startingAfterDbId(String dbId) {
            this.startingAfterDbId = dbId;
            return this;
        }

        /**
         * 
         */
        public DatabaseFilterBuilder provider(CloudProviderType p) {
            this.provider = p;
            return this;
        }

        /**
         * 
         */
        public DatabaseFilterBuilder include(Include i) {
            this.include = i;
            return this;
        }

        /**
         * 
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
