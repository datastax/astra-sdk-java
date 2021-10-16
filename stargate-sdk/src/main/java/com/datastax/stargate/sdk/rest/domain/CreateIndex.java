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

package com.datastax.stargate.sdk.rest.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Creation request for an INDEX.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CreateIndex implements Serializable {

    /** Serial Number. */
    private static final long serialVersionUID = -5374080080154230782L;
    
    /** Limited values for the kind. */
    private static enum IndexKind { KEYS, VALUES, ENTRIES, FULL }
    
    /** Constant for a SASI index. */
    public static final String TYPE_SASI = "org.apache.cassandra.index.sasi.SASIIndex";
    
    /** Constant for a SAI index. */
    public static final String TYPE_SAI  = "org.apache.cassandra.index.sai.StorageAttachedIndex";
    
    /** Identifer of the index. */
    private String name;
    
    /** Name of the column. */
    private final String column;
  
    /** CREATE IF NOT EXIST. */
    private final boolean ifNotExists;
    
    /** Custom type. */
    private final String type;
    
    /** Default value for index. */
    private final IndexKind kind;
    
    /** Index options. */
    private final Map<String, String> options;

    /**
     * Constructor.
     *
     * @param builder
     *      current builder
     */
    private CreateIndex(CreateIndexBuilder builder) {
        this.name         = builder.name;
        this.column       = builder.column;
        this.ifNotExists  = builder.ifNotExists;
        this.type         = builder.type;
        this.kind         = builder.kind;
        this.options      = builder.options;
    }
    
    /**
     * Access to builder.
     *
     * @return
     *      create index builder
     */
    public static CreateIndexBuilder builder() {
        return new CreateIndexBuilder();
    }
    
    /**
     * Internal builder
     *
     * @author Cedrick LUNVEN (@clunven)
     *
     */
    public static class CreateIndexBuilder {
        
        /** attribute exist. */
        boolean ifNotExists = false;
        
        /** attribute name. */
        String name;
        
        /** attribute column. */
        String column;
        
        /** attribute type. */
        String type = null;
        
        /** attribute kind. */
        IndexKind kind = null;
        
        /** list of options. */
        Map<String, String> options = null;
        
        /**
         * Catalog to build.
         * 
         * @return
         *      current instace.
         */
        public CreateIndex build() {
            return new CreateIndex(this);
        }
        
        /**
         * Helper for exists.
         *
         * @param ine
         *      value for exists
         * @return
         *      self reference
         */
        public CreateIndexBuilder ifNotExist(boolean ine) {
            this.ifNotExists = ine;
            return this;
        }
        /**
         * Helper for name.
         * 
         * @param name
         *      name
         * @return
         *      self reference

         */
        public CreateIndexBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * Helper for type.
         * @param t
         *          type
         * @return
         *      self reference

         */
        public CreateIndexBuilder type(String t) {
            this.type = t;
            return this;
        }
        
        /**
         * Helper for sasi.
         *
         * @return
         *      self reference
         */
        public CreateIndexBuilder sasi() {
            return type(TYPE_SASI);
        }
        
        /**
         * Helper for SAI.
         * 
         * @return
         *      self reference
         */
        public CreateIndexBuilder sai() {
            return type(TYPE_SAI);
        }
        
        /**
         * Helper for column.
         *
         * @param name
         *      column name
         * @return
         *      self reference
         */
        public CreateIndexBuilder column(String name) {
            this.column = name;
            return this;
        }
        
        /**
         * Helper for kind.
         * @param k
         *          kind
         * @return
         *      self reference
         */
        public CreateIndexBuilder kind(IndexKind k) {
            this.kind = k;
            return this;
        }
        
        /**
         * Help for options.
         * 
         * @param key
         *       key 
         * @param value
         *          value
         * @return
         *      self reference
         */
        public CreateIndexBuilder addOption(String key, String value) {
            if (options == null) {
                options = new HashMap<>();
            }
            this.options.put(key, value);
            return this;
        }
    }

    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter accessor for attribute 'column'.
     *
     * @return
     *       current value of 'column'
     */
    public String getColumn() {
        return column;
    }

    /**
     * Getter accessor for attribute 'ifNotExists'.
     *
     * @return
     *       current value of 'ifNotExists'
     */
    public boolean isIfNotExists() {
        return ifNotExists;
    }

    /**
     * Getter accessor for attribute 'type'.
     *
     * @return
     *       current value of 'type'
     */
    public String getType() {
        return type;
    }

    /**
     * Getter accessor for attribute 'kind'.
     *
     * @return
     *       current value of 'kind'
     */
    public IndexKind getKind() {
        return kind;
    }

    /**
     * Getter accessor for attribute 'options'.
     *
     * @return
     *       current value of 'options'
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * Setter accessor for attribute 'name'.
     * @param name
     * 		new value for 'name '
     */
    public void setName(String name) {
        this.name = name;
    }

}
