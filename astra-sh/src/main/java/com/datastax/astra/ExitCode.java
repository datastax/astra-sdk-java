package com.datastax.astra;

/**
 * Normalization of exit codes.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum ExitCode {
    
    /** code ok. */
    SUCCESS(0),
    
    /** code. */
    INVALID_PARAMETER(400),
    
    /** code. */
    PARSE_ERROR(-1),
    
    /** code. */
    CANNOT_CONNECT(-10);
    
    /* Exit code. */
    private int code;
    
    /**
     * Constructor.
     *
     * @param code
     *      target code
     */
    private ExitCode(int code) {
        this.code = code;
    }

    /**
     * Getter accessor for attribute 'code'.
     *
     * @return
     *       current value of 'code'
     */
    public int getCode() {
        return code;
    }
    
    /**
     * Exit the prgram.
     */
    public void exit() {
        System.exit(code);
    }
     
    

}
