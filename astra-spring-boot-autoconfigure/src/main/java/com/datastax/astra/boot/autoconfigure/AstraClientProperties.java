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

package com.datastax.astra.boot.autoconfigure;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Load the client properties. 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@ConfigurationProperties(prefix = "astra")
public class AstraClientProperties {
    
    /** Configuration regarding the Api. */
    private Api api;

    /** Configuration regarding CQL sessions. */
    private Cql cql;
    
    /**
     * Getter accessor for attribute 'api'.
     *
     * @return
     *       current value of 'api'
     */
    public Api getApi() {
        return api;
    }

    /**
     * Setter accessor for attribute 'api'.
     * @param api
     *      new value for 'api '
     */
    public void setApi(Api api) {
        this.api = api;
    }

    /**
     * Getter accessor for attribute 'cql'.
     *
     * @return
     *       current value of 'cql'
     */
    public Cql getCql() {
        return cql;
    }

    /**
     * Setter accessor for attribute 'cql'.
     * @param cql
     *      new value for 'cql '
     */
    public void setCql(Cql cql) {
        this.cql = cql;
    }
    
    /**
     * Nested properties for gRPC.
     */
    public static class Grpc {
        
        /** flag to enable gRPC. */
        private boolean enabled;

        /**
         * Getter accessor for attribute 'enabled'.
         *
         * @return
         *       current value of 'enabled'
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Setter accessor for attribute 'enabled'.
         * @param enabled
         *      new value for 'enabled '
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Specialization for the APIS.
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class Api {
        
        /** Application Token. */
        private String applicationToken;
        
        /** Database unique identifier.  */
        private String databaseId;
        
        /** Astra database region. */
        private String databaseRegion;
        
        /** Configuration regarding gRPC. */
        private Grpc grpc;
        
        /**
         * Getter accessor for attribute 'databaseId'.
         *
         * @return
         *       current value of 'databaseId'
         */
        public String getDatabaseId() {
            return databaseId;
        }

        /**
         * Setter accessor for attribute 'databaseId'.
         * @param databaseId
         *      new value for 'databaseId '
         */
        public void setDatabaseId(String databaseId) {
            this.databaseId = databaseId;
        }

        /**
         * Getter accessor for attribute 'applicationToken'.
         *
         * @return
         *       current value of 'applicationToken'
         */
        public String getApplicationToken() {
            return applicationToken;
        }

        /**
         * Setter accessor for attribute 'applicationToken'.
         * @param applicationToken
         *      new value for 'applicationToken '
         */
        public void setApplicationToken(String applicationToken) {
            this.applicationToken = applicationToken;
        }
        
        /**
         * Getter accessor for attribute 'databaseRegion'.
         *
         * @return
         *       current value of 'databaseRegion'
         */
        public String getDatabaseRegion() {
            return databaseRegion;
        }

        /**
         * Setter accessor for attribute 'databaseRegion'.
         * @param databaseRegion
         *      new value for 'databaseRegion '
         */
        public void setDatabaseRegion(String databaseRegion) {
            this.databaseRegion = databaseRegion;
        }

        /**
         * Getter accessor for attribute 'grpc'.
         *
         * @return
         *       current value of 'grpc'
         */
        public Grpc getGrpc() {
            return grpc;
        }

        /**
         * Setter accessor for attribute 'grpc'.
         * @param grpc
         *      new value for 'grpc '
         */
        public void setGrpc(Grpc grpc) {
            this.grpc = grpc;
        }
        
    }
    
    /**
     * Special key for Metrics
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class Cql {
        
        /** Enable Cql Configuration. */
        private boolean enabled;
        
        /** Download SCB. */
        private DownloadSecureBundle downloadScb;

        /** Get Values. */
        private Metrics metrics;
        
        /**
         * Getter accessor for attribute 'enabled'.
         *
         * @return
         *       current value of 'enabled'
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Setter accessor for attribute 'enabled'.
         * @param enabled
         *      new value for 'enabled '
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        /**
         * Getter accessor for attribute 'metrics'.
         *
         * @return
         *       current value of 'metrics'
         */
        public Metrics getMetrics() {
            return metrics;
        }

        /**
         * Setter accessor for attribute 'metrics'.
         * @param metrics
         *      new value for 'metrics '
         */
        public void setMetrics(Metrics metrics) {
            this.metrics = metrics;
        }

        /**
         * Getter accessor for attribute 'downloadScb'.
         *
         * @return
         *       current value of 'downloadScb'
         */
        public DownloadSecureBundle getDownloadScb() {
            return downloadScb;
        }

        /**
         * Setter accessor for attribute 'downloadScb'.
         * @param downloadScb
         * 		new value for 'downloadScb '
         */
        public void setDownloadScb(DownloadSecureBundle downloadScb) {
            this.downloadScb = downloadScb;
        }
        
    }
    
    /**
     * Cql Metrics special properties
     */
    public static class Metrics {
        
        /** flag for the metrics. */
        private boolean enabled;

        /**
         * Getter accessor for attribute 'enabled'.
         *
         * @return
         *       current value of 'enabled'
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Setter accessor for attribute 'enabled'.
         * 
         * @param enabled
         *      new value for 'enabled '
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    
    /**
     * Code for the download of the clud seure bundle
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class DownloadSecureBundle {
        
        /** Enable Download. */
        private boolean enabled = true;

        /** Path to Download. */
        private String path = System.getProperty("user.home") + File.separator + ".astra";
        
        /**
         * Getter accessor for attribute 'enabled'.
         *
         * @return
         *       current value of 'enabled'
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Setter accessor for attribute 'enabled'.
         * @param enabled
         *      new value for 'enabled '
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Getter accessor for attribute 'path'.
         *
         * @return
         *       current value of 'path'
         */
        public String getPath() {
            return path;
        }

        /**
         * Setter accessor for attribute 'path'.
         * @param path
         * 		new value for 'path '
         */
        public void setPath(String path) {
            this.path = path;
        }
        
    }

}
