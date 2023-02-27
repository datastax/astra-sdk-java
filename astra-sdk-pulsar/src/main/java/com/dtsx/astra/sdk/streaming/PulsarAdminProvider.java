package com.dtsx.astra.sdk.streaming;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.function.Supplier;

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
     * @param webServiceUrl
     *      webServiceUrl Url
     * @param pulsarToken
     *      pulsar token
     */
    public PulsarAdminProvider(String webServiceUrl, String pulsarToken) {
        try {
            pulsarAdmin = PulsarAdmin.builder()
                   .allowTlsInsecureConnection(false)
                   .enableTlsHostnameVerification(true)
                   .useKeyStoreTls(false)
                   .tlsTrustStoreType("JKS")
                   .tlsTrustStorePath("")
                   .tlsTrustStorePassword("")
                   .serviceHttpUrl(webServiceUrl)
                   .authentication(AuthenticationFactory.token(pulsarToken))
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
