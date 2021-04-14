package io.stargate.sdk.rest.domain;

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
     * @param builder
     */
    private CreateIndex(CreateIndexBuilder builder) {
        this.name         = builder.name;
        this.column       = builder.column;
        this.ifNotExists  = builder.ifNotExists;
        this.type         = builder.type;
        this.kind         = builder.kind;
        this.options      = builder.options;
    }
    
    public static CreateIndexBuilder builder() {
        return new CreateIndexBuilder();
    }
    
    public static class CreateIndexBuilder {
        boolean ifNotExists = false;
        String name;
        String column;
        String type = null;
        IndexKind kind = null;
        Map<String, String> options = null;
        
        public CreateIndex build() {
            return new CreateIndex(this);
        }
        public CreateIndexBuilder ifNotExist(boolean ine) {
            this.ifNotExists = ine;
            return this;
        }
        public CreateIndexBuilder name(String name) {
            this.name = name;
            return this;
        }
        public CreateIndexBuilder type(String t) {
            this.type = t;
            return this;
        }
        public CreateIndexBuilder sasi() {
            return type(TYPE_SASI);
        }
        public CreateIndexBuilder column(String name) {
            this.column = name;
            return this;
        }
        public CreateIndexBuilder kind(IndexKind k) {
            this.kind = k;
            return this;
        }
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
