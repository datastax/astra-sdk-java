package com.dstx.astrarc;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.utils.AstraRc;


public class AstraRcTest {
    
    @Test
    public void should_create_astraRc_File() {
        // Given
        new File(System.getProperty("user.home") + "/.astrarc").delete();
        Assert.assertFalse(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // When
        AstraRc.create(new ApiDevopsClient(System.getenv("bearerToken")));
        // Then
        Assert.assertTrue(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // Then we should be able to load the file
        AstraRc.load();
    }
    
    @Test
    public void should_initAstraClien_from_File() {
        AstraClient astraClient = AstraClient.builder().build();
        Assertions.assertNotNull(astraClient);
    }
    
    

}
