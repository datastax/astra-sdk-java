package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
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
     * @param tenant
     *      current tenant
     */
    public PulsarClientProvider(Tenant tenant) {
        this(tenant.getBrokerServiceUrl(), tenant.getPulsarToken());
    }

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

    /**
     * Syntax Sugar.
     *
     * @param tenant
     *     current tenant
     * @return
     *      current pulsar client
     */
    public static PulsarClient of(Tenant tenant) {
        return new PulsarClientProvider(tenant).get();
    }

    /**
     * Access a pulsar token from minimal information.
     *
     * @param astraToken
     *      astra token
     * @param tenantName
     *      tenant name
     * @return
     *      pulsar client
     */
    public static PulsarClient of(String astraToken, String tenantName) {
        return of(astraToken, AstraEnvironment.PROD, tenantName);
    }

    /**
     * Access a pulsar token from minimal information.
     *
     * @param astraToken
     *      astra token
     * @param env
     *      astra
     * @param tenantName
     *      tenant name
     * @return
     *      pulsar client
     */
    public static PulsarClient of(String astraToken, AstraEnvironment env, String tenantName) {
        return new PulsarClientProvider(new TenantClient(astraToken, env, tenantName).find().get()).get();
    }

    /** {@inheritDoc} */
    @Override
    public PulsarClient get() {
        return pulsarClient;
    }

}
