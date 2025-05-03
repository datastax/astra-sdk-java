package com.dtsx.astra.sdk.utils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpClientWrapperTest {
    @Mock
    private ClassicHttpResponse mockResponse;

    private HttpClientWrapper httpClient;
    private HttpGet request;

    @BeforeEach
    void setup() {
        httpClient = HttpClientWrapper.getInstance("test");
        request = new HttpGet("http://test.com");
        request.addHeader("Authorization", "Bearer test-token");
        HttpClientWrapper.resetRetryConfiguration();
    }

    @AfterEach
    void cleanup() {
        HttpClientWrapper.resetRetryConfiguration();
    }

    @Test
    void shouldRetryOn500Error() throws Exception {
        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockResponse.getEntity()).thenReturn(new StringEntity("Server Error"));
        AtomicInteger attemptCount = new AtomicInteger(0);
        httpClient.httpClient = mock(CloseableHttpClient.class);
        when(httpClient.httpClient.execute(any())).thenAnswer(invocation -> {
            int attempt = attemptCount.incrementAndGet();
            if (attempt <= 2) {
                return mockResponse;
            }
            ClassicHttpResponse successResponse = mock(ClassicHttpResponse.class);
            when(successResponse.getCode()).thenReturn(HttpStatus.SC_OK);
            when(successResponse.getEntity()).thenReturn(new StringEntity("Success"));
            return successResponse;
        });
        ApiResponseHttp response = httpClient.executeHttp(request, true);
        assertEquals(HttpStatus.SC_OK, response.getCode());
        assertEquals("Success", response.getBody());
        assertEquals(3, attemptCount.get());
    }

    @Test
    void shouldRespectMaxRetries() throws Exception {
        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockResponse.getEntity()).thenReturn(new StringEntity("Server Error"));
        HttpClientWrapper.configureRetry(2);
        AtomicInteger attemptCount = new AtomicInteger(0);
        httpClient.httpClient = mock(CloseableHttpClient.class);
        when(httpClient.httpClient.execute(any())).thenAnswer(invocation -> {
            attemptCount.incrementAndGet();
            return mockResponse;
        });
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> httpClient.executeHttp(request, true));
        assertTrue(exception.getMessage().contains("code=500"));
        assertEquals(3, attemptCount.get());
    }

    @Test
    void shouldUseExponentialBackoff() throws Exception {
        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockResponse.getEntity()).thenReturn(new StringEntity("Server Error"));
        HttpClientWrapper.configureRetry(3, 100, 2.0);
        AtomicInteger attemptCount = new AtomicInteger(0);
        httpClient.httpClient = mock(CloseableHttpClient.class);
        when(httpClient.httpClient.execute(any())).thenAnswer(invocation -> {
            int attempt = attemptCount.incrementAndGet();
            if (attempt <= 2) {
                return mockResponse;
            }
            ClassicHttpResponse successResponse = mock(ClassicHttpResponse.class);
            when(successResponse.getCode()).thenReturn(HttpStatus.SC_OK);
            when(successResponse.getEntity()).thenReturn(new StringEntity("Success"));
            return successResponse;
        });
        long startTime = System.currentTimeMillis();
        ApiResponseHttp response = httpClient.executeHttp(request, true);
        long endTime = System.currentTimeMillis();
        assertEquals(HttpStatus.SC_OK, response.getCode());
        assertTrue(endTime - startTime >= 300);
    }

    @Test
    void shouldNotRetryOn400Error() throws Exception {
        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(mockResponse.getEntity()).thenReturn(new StringEntity("Bad Request"));
        httpClient.httpClient = mock(CloseableHttpClient.class);
        when(httpClient.httpClient.execute(any())).thenAnswer(invocation -> mockResponse);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> httpClient.executeHttp(request, true));
        assertTrue(exception.getMessage().contains("HTTP_BAD_REQUEST"));
        verify(httpClient.httpClient, times(1)).execute(any());
    }

    @Test
    void shouldHandleInterruptedException() throws Exception {
        httpClient.httpClient = mock(CloseableHttpClient.class);
        when(httpClient.httpClient.execute(any())).thenThrow(new InterruptedException("Test interruption"));
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> httpClient.executeHttp(request, true));
        assertTrue(exception.getMessage().contains("Request interrupted"));
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void shouldResetRetryConfiguration() {
        HttpClientWrapper.configureRetry(5, 2000, 1.5);
        HttpClientWrapper.resetRetryConfiguration();
        assertEquals(3, HttpClientWrapper.maxRetries);
        assertEquals(1000, HttpClientWrapper.retryInitialDelayMs);
        assertEquals(2.0, HttpClientWrapper.retryBackoffMultiplier);
    }
} 