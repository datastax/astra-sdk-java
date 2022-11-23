package io.stargate.sdk.loadbalancer;

/**
 * Error when no resources are enabled anymore 
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class NoneResourceAvailableException extends RuntimeException {

    /** Serial. */
    private static final long serialVersionUID = 104799470427233094L;
    
    /**
     * Error with message
     *
     * @param msg
     *      current message
     */
    public NoneResourceAvailableException(String msg) {
        super(msg);
    }
    
    /**
     * Error with message and error.
     *
     * @param msg
     *      current message
     * @param parent
     *      error
     */
    public NoneResourceAvailableException(String msg, Throwable parent) {
        super(msg, parent);
    }

}
