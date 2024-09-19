package com.dtsx.astra.sdk.utils.observability;

import com.dtsx.astra.sdk.utils.ApiResponse;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import lombok.Getter;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Encapsulates detailed information about the execution of a command, including the original request,
 * the raw response, HTTP response details, and timing information. This class serves as a comprehensive
 * record of a command's execution, facilitating analysis, logging, and monitoring of command operations.
 */
@Getter
public class ApiExecutionInfos implements Serializable {


    /**
     * The original command request that was executed. This field provides access to the details of the
     * command that triggered the execution, allowing observers to understand what operation was performed.
     */
    private final Object request;

    /**
     * Name of the Operation for Devops API
     */
    private final String operationName;

    /**
     * A map containing the HTTP headers from the request.
     */
    private final Map<String, List<String>> requestHttpHeaders;

    /**
     * HTTP Request in
     */
    private final Method requestHttpMethod;

    /**
     * Request URL
     */
    private final String requestUrl;

    /**
     * The raw {@link ApiResponse} received in response to the command execution. This field contains the
     * complete response from the server, including any data, errors, or status information returned.
     */
    private final ApiResponse<?> response;

    /**
     * The raw {@link ApiResponse} received in response to the command execution. This field contains the
     * complete response from the server, including any data, errors, or status information returned.
     */
    private final String responseBody;

    /**
     * The HTTP status code returned by the server in response to the command execution. This code provides
     * a standard way to indicate the result of the HTTP request (e.g., success, error, not found).
     */
    private final int responseHttpCode;

    /**
     * A map containing the HTTP headers from the response. These headers can provide additional context about
     * the response, such as content type, caching policies, and other metadata.
     */
    private final Map<String, String> responseHttpHeaders;

    /**
     * The duration of time, in milliseconds, that the command execution took, from sending the request to
     * receiving the response. This timing information can be used for performance monitoring and optimization.
     */
    private final long executionTime;

    /**
     * The timestamp marking when the command execution was initiated. This information is useful for logging
     * and monitoring purposes, allowing for the temporal correlation of command executions within the system.
     */
    private final Instant executionDate;

    /**
     * Constructor with the builder.
     *
     * @param builder
     *      current builder.
     */
    private ApiExecutionInfos(ApiExecutionInfoBuilder builder) {
        this.operationName       = builder.operationName;
        this.requestHttpMethod   = builder.requestHttpMethod;
        this.request             = builder.payload;
        this.requestHttpHeaders  = builder.requestHttpHeaders;
        this.response            = builder.response;
        this.responseHttpHeaders = builder.responseHttpHeaders;
        this.responseBody        = builder.responseBody;
        this.responseHttpCode    = builder.responseHttpCode;
        this.executionTime       = builder.executionTime;
        this.executionDate       = builder.executionDate;
        this.requestUrl          = builder.requestUrl;
    }

    /**
     * Initialize our custom builder.
     *
     * @return
     *      builder
     */
    public static ApiExecutionInfoBuilder builder() {
        return new ApiExecutionInfoBuilder();
    }

    /**
     * Builder class for execution information
     */
    public static class ApiExecutionInfoBuilder {
        private String operationName;
        private Object payload;
        private Method requestHttpMethod;
        private ApiResponse<?> response;
        private long executionTime;
        private int responseHttpCode;
        private String responseBody;
        private Map<String, List<String>> requestHttpHeaders;
        private Map<String, String> responseHttpHeaders;
        private final Instant executionDate;
        private String requestUrl;

        /**
         * Default constructor.
         */
        public ApiExecutionInfoBuilder() {
            this.executionDate = Instant.now();
        }

        /**
         * Populate after http call.
         *
         * @param payload
         *      current payload
         * @return
         *      current reference
         */
        public ApiExecutionInfoBuilder withRequestPayload(Object payload) {
            this.payload = payload;
            return this;
        }

        /**
         * Operation Name.
         *
         * @param operationName
         *     name of current operation
         * @return
         *      current reference
         */
        public ApiExecutionInfoBuilder withOperationName(String operationName) {
            this.operationName =operationName;
            return this;
        }

        /**
         * Populate after http call.
         *
         * @param req
         *      input http request
         * @return
         *     current reference
         */
        public ApiExecutionInfoBuilder withHttpRequest(HttpUriRequestBase req) {
            this.requestHttpMethod = Method.valueOf(req.getMethod());
            this.requestHttpHeaders = Arrays.stream(req.getHeaders()).collect
                    (Collectors.toMap(NameValuePair::getName,
                            h -> Collections.singletonList(h.getValue())));
            try {
                this.requestUrl = req.getUri().toString();
            } catch (Exception e) {}
            if (req.getEntity() != null) {
                try {
                    this.payload = EntityUtils.toString(req.getEntity());
                } catch (Exception e) {}
            }
            return this;
        }

        /**
         * Populate after http call.
         *
         * @param httpResponse http response
         */
        public void withHttpResponse(ApiResponseHttp httpResponse) {
            Assert.notNull(httpResponse, "httpResponse");
            this.executionTime       = System.currentTimeMillis() - 1000 * executionDate.getEpochSecond();
            this.responseHttpCode    = httpResponse.getCode();
            this.responseHttpHeaders = httpResponse.getHeaders();
            this.responseBody = httpResponse.getBody();
        }

        /**
         * Invoke constructor with the builder.
         *
         * @return
         *      immutable instance of execution infos.
         */
        public ApiExecutionInfos build() {
            return new ApiExecutionInfos(this);
        }

    }

}
