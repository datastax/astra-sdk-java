package com.datastax.stargate.sdk.audit;

public interface ApiCallListener {

    void onCall(ApiCallEvent event);
    
    
}
