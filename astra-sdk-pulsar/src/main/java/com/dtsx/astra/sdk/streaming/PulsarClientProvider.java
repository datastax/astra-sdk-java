package com.dtsx.astra.sdk.streaming;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.function.Supplier;

/**
 * Delegate PulsarClient to a sub class.
 */
public class PulsarClientProvider implements Supplier<PulsarClient>{
    
    /* Use as singleton. */
    private PulsarClient pulsarClient;
    
    /**
     * Default constructor.
     * 
     * @param brokerUrl
     *      broker Url
     * @param pulsarToken
     *      pulsar token
     */
    public PulsarClientProvider(String brokerUrl, String pulsarToken) {
        try {
            pulsarClient = PulsarClient.builder()
                    .serviceUrl(brokerUrl)
                    .authentication(AuthenticationFactory.token(pulsarToken))
                    .build();
        } catch (PulsarClientException e) {
            throw new IllegalArgumentException("Cannot connect to pulsar", e); 
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public PulsarClient get() {
        return pulsarClient;
    }

}
