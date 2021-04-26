package com.datastax.astra.sdk.utils;

/**
 * Utility to work with generated CSV
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TokenCsv {
    
    /**
     * Hide constructor.
     */
    private TokenCsv() {}
    
    public static final Token readCsvToken(String file) {
        return new Token("", "", "", "");
        
    }

}
