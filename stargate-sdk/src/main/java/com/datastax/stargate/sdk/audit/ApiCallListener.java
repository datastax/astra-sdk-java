package com.datastax.stargate.sdk.audit;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;

public interface ApiCallListener {

    void onHttpCall(ClassicHttpRequest req, CloseableHttpResponse res);
    
}
