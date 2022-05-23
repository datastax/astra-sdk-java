package com.datastax.astra.cmd;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Operation in the Shell.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Argument implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -8583397363459549465L;

    /** name of the argument. */
    private final String name;
    
    /** Legal values for this argument. */
    private final Set<String> fixedValues;
    
    /** if required. */
    private final boolean required;

    /** Documentation. */
    private final String description;
    
    /**
     * Constructor through builder.
     *
     * @param b
     *      builder
     */
    private Argument(ArgumentBuilder b) {
        super();
        this.name = b.name;
        this.required = b.required;
        this.description = b.description;
        this.fixedValues = b.fixedValues;
    }
    
    /**
     * Create a builder.
     *
     * @return
     */
    public static ArgumentBuilder builder() {
        return new ArgumentBuilder();
    }
    
    /**
     * Builder class.
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class ArgumentBuilder {
        
        /** name of the argument. */
        private String name;
        
        /** Legal values for this argument. */
        private Set<String> fixedValues = new HashSet<>();
        
        /** if required. */
        private boolean required = false;

        /** Documentation. */
        private String description;
        
        public Argument build() {
            return new Argument(this);
        }

        /**
         * Setter accessor for attribute 'name'.
         *
         * @param name
         * 		new value for name
         * @return
         *      reference to current object
         */
        public ArgumentBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * Add a fixed value to argument.
         *
         * @param vals
         *      all fixed values
         * @return
         *      reference to current object
         */
        public ArgumentBuilder fixedValues(String... vals) {
            return fixedValues(Arrays.asList(vals));
        }
        
        /**
         * Set fixed values.
         * @param vals
         *      values
         * @return
         *       reference to current object
         */
        public ArgumentBuilder fixedValues(List<String> vals) {
            fixedValues = new HashSet<String>(vals);
            return this;
        }

       
        /**
         * Set a argument as mandatory.
         * 
         * @return
         *     reference to current object
         */
        public ArgumentBuilder required() {
            this.required = true;
            return this;
        }

        
        /**
         * Populate description.
         * 
         * @param description
         *      value for description 
         * @return
         *      reference to current object
         */
        public  ArgumentBuilder description(String description) {
            this.description = description;
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
     * Getter accessor for attribute 'required'.
     *
     * @return
     *       current value of 'required'
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Getter accessor for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter accessor for attribute 'fixedValues'.
     *
     * @return
     *       current value of 'fixedValues'
     */
    public Set<String> getFixedValues() {
        return fixedValues;
    }

}
