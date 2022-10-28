package com.dtsx.astra.sdk.streaming;

import java.util.function.Supplier;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import com.dtsx.astra.sdk.streaming.domain.Tenant;

/**
 * Delegate PulsarClient to a sub class.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class PulsarClientProvider implements Supplier<PulsarClient>{
    
    /* Use as singleton. */
    private PulsarClient pulsarClient;
    
    /**
     * Default constructor.
     * 
     * @param tenant
     *      parent tenant
     */
    public PulsarClientProvider(Tenant tenant) {
        try {
            pulsarClient = PulsarClient.builder()
                    .serviceUrl(tenant.getBrokerServiceUrl())
                    .authentication(AuthenticationFactory.token(tenant.getPulsarToken()))
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
