package com.datastax.stargate.sdk.core;

import java.util.function.Supplier;

/**
 * To work the APi needs a token. 
 * It can be static or dynamically generated.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ApiTokenProvider extends Supplier<String> {
    
    /** {@inheritDoc} */
    @Override
    default String get() {
        return getToken();
    }
    
    /**
     * Building the token.
     *
     * @return
     *      current token
     */
    String getToken();

}
