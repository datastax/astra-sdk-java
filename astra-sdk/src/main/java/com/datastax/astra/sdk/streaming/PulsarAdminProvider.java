package com.datastax.astra.sdk.streaming;

import java.util.function.Supplier;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClientException;

import com.datastax.astra.sdk.streaming.domain.Tenant;

/**
 * Delegate PulsarClient to a sub class.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class PulsarAdminProvider implements Supplier<PulsarAdmin>{
    
    /* Use as singleton. */
    private PulsarAdmin pulsarAdmin;
    
    /**
     * Default constructor.
     * 
     * @param tenant
     *      parent tenant
     */
    public PulsarAdminProvider(Tenant tenant) {
        try {
            pulsarAdmin = PulsarAdmin.builder()
                   .allowTlsInsecureConnection(false)
                   .enableTlsHostnameVerification(true)
                   .useKeyStoreTls(false)
                   .tlsTrustStoreType("JKS")
                   .tlsTrustStorePath("")
                   .tlsTrustStorePassword("")
                   .serviceHttpUrl(tenant.getWebServiceUrl())
                   .authentication(AuthenticationFactory.token(tenant.getPulsarToken()))
                   .build();
         } catch (PulsarClientException e) {
             throw new IllegalArgumentException("Cannot use Pulsar admin", e);
         }
    }
    
    /** {@inheritDoc} */
    @Override
    public PulsarAdmin get() {
        return pulsarAdmin;
    }

}
