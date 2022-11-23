package io.stargate.sdk.doc.domain;

/**
 * List built-in functions in a namespace.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum DocumentFunction {
    
    /**
     * Add an item to a list.
     */
    PUSH("$push"),
    
    /**
     * Pop an item from a list.
     */
    POP("$pop");
    
    private String operation;
    
    /**
     * Initialization of a function.
     *
     * @param op
     *      current operation
     */
    private DocumentFunction(String op) {
        this.operation = op;
    }
    
    /**
     * Getter for the operation.
     *
     * @return
     *      target operation
     */
    public String getOperation() {
        return operation;
    }

}
