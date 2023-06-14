package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.utils.ApiLocator;
import org.junit.jupiter.api.Test;

public class ApiDevopsDevTest {

    @Test
    public void shouldWorkOnDev() {

        AstraDevopsApiClient clientDev = new AstraDevopsApiClient(
                "AstraCS:oLCrIRvsXYENogiZZFBMnjkr:d9431ffadca2227003099d827317a6bce159e301cbe5a37800bd3aaa8c17306b",
                ApiLocator.AstraEnvironment.DEV);
        System.out.println(clientDev.getOrganization().getId());

        AstraDevopsApiClient clientTestProd = new AstraDevopsApiClient(
                "AstraCS:uZclXTYecCAqPPjiNmkezapR:e87d6edb702acd87516e4ef78e0c0e515c32ab2c3529f5a3242688034149a0e4\n",
                ApiLocator.AstraEnvironment.PROD);
        System.out.println(clientTestProd.getOrganization().getId());

    }
}
