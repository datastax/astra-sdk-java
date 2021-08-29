package com.datastax.astra.sdk.doc;

import org.junit.jupiter.api.Test;

import com.datastax.astra.sdk.utils.ApiLocator;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.utils.HttpApisClient;

public class DummyTest {
    
    @Test
    public void listNamespaces() {
        HttpApisClient.getInstance().setVerbose(true);
        
        String token    = "AstraCS:llRNYnYgMgSQeybABhNmDAHW:a3ff5ca604cc0c52b70dea2fa61ec2bd8c871c3036fdd12d304feef09bd5030a";
        String endPoint = ApiLocator
                .getApiRestEndpoint("87f6d8f1-a78b-4a0c-84a0-8922107ad4d0", "eu-central-1");
        
        new ApiDocumentClient(endPoint, token)
                .namespaceNames()
                .forEach(System.out::println);
    }

}
