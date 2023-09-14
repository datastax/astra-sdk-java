package com.dtsx.astra.sdk.ann;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

public class TestAnnotation {

    @Test
    @EnableIfAstraToken
    public void testMethod() {
        System.out.println("Astra Token is here ");
    }

    @Test
    @EnableIfOpenAI
    public void testEnv2() {
        System.out.println("Open AI is here");
    }

    @Test
    @EnableIfOpenAI
    @EnableIfAstraToken
    public void testBoth() {
        System.out.println("Both");
    }


}
