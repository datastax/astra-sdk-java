package com.datastax.astra.sdk;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.datastax.astra.sdk.iam.IamClient;

@TestMethodOrder(OrderAnnotation.class)
public class T09_Devops_Streaming_IntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[Astra DEVOPS ROLES Test Suite]" + ANSI_RESET);
        appToken = Optional.ofNullable("AstraCS:abjIANeldqrcOQmILeACwhOr:5daf79ff81bd667ea29179eac08ae98e047c0f42ad462bf66b2282b372eb641a");
    }
    
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        System.out.println(ANSI_YELLOW + "- Parameter validation" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IamClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IamClient(null));
    }
    
    @Test
    @Order(2)
    public void should_list_providers() {
        
    }

}
