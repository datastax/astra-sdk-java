package com.dtsx.astra.sdk.streaming;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.policies.data.PersistentTopicInternalStats;
import org.junit.jupiter.api.Test;

public class PulsarAdminAstraStreamingTest {

    String pulsarToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2NzcyNDM1NzYsImlzcyI6ImRhdGFzdGF4Iiwic3Vi" +
            "IjoiY2xpZW50O2Y5NDYwZjE0LTk4NzktNGViZS04M2YyLTQ4ZDNmM2RjZTEzYztZMngxYmkxblkzQXRaV0Z6ZERFPTtk" +
            "MzlkZWMyYTk0IiwidG9rZW5pZCI6ImQzOWRlYzJhOTQifQ.RtQwhbak_gEF3AHy9HZDVsJEoo5TWUDDZTeB-Zyp4vyh4" +
            "uPAWy_PEPrCdGBZtUuHr5mD9lTjk_v9gCTDzwJEK_QO51lta1ZfN-ZpwpZ3o_bOhXy1p-atmsQzW6-13ilUAONsZCC7W" +
            "Ey_Ul0iq7zE64tGfCWNMzaj5xRBDb8SBiT9ikowKGM1Xr5RD5QWmTK_TY_r6uN79JIZcpWHjj_l_354KNl1--1HEEST3" +
            "wyifJotETSEczvD9Y2RLrEqKe7i9tWetC5oQUTorqJgnEKd9-EvbwFh1cEgY2jo18thuyG31n8KdwRrduv02NCJv-zpT" +
            "sVOyxujAKjzXytEixDjsA";
    @Test
    public void testPulsarAdmin() throws PulsarAdminException {
        System.out.println("ok");
        PulsarAdmin pa = new PulsarAdminProvider(
                "https://pulsar-gcp-useast1.api.streaming.datastax.com",pulsarToken)
                .get();

        // Get Infos
        System.out.println(pa.tenants().getTenantInfo("clun-gcp-east1").toString());

        // List Namespaces
        pa.namespaces().getNamespaces("clun-gcp-east1").forEach(System.out::println);

        // List Topics
        pa.topics().getList("clun-gcp-east1/astracdc").forEach(System.out::println);

        // Details on a topic
        PersistentTopicInternalStats stats = pa.topics()
           .getInternalStats("persistent://clun-gcp-east1/astracdc/log-578d5f2f-dd61-4ab9-a07e-8ffdaff60fd8-ks1.user-partition-1");
        System.out.println(stats.totalSize);







    }

    @Test
    public void testPulsarClient() throws PulsarClientException {
        PulsarClient cli =
                new PulsarClientProvider("pulsar+ssl://pulsar-gcp-useast1.streaming.datastax.com:6651", pulsarToken).get();


    }
}
