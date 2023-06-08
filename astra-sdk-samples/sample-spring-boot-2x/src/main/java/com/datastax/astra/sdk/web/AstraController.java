package com.datastax.astra.sdk.web;

import com.datastax.astra.sdk.AstraClient;
import com.dtsx.astra.sdk.org.domain.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;

/**
 * Access Astra Client if needed
 */
@Controller
public class AstraController {

    /** Inject Astra Client, access to all APIs */
    @Autowired
    private AstraClient astraClient;

    /**
     * Sample Devops Api Call.
     *
     * @return
     *      current organization
     */
    @GetMapping("/astra/organization")
    public Organization hello() {
        return astraClient.apiDevops().getOrganization();
    }

    @PostConstruct
    public void test() {
        System.out.println(astraClient.apiDevops().getOrganization().getId());
    }


}
