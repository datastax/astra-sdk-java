package com.dtsx.astra.sdk.huggingface;


import io.stargate.sdk.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HFDatasetClient {

    public static final String ENDPOINT_BASE = "https://datasets-server.huggingface.co/rows";

    public static String buildEndpoint(String org, String dataset, String split, int offset, int limit) {

        return String.format("%s?dataset=%s%%2F%s&config=default&split=%s&offset=%d&limit=%d", ENDPOINT_BASE, org, dataset, split, offset, limit);
    }

    @Test
    public void shoulShowDataset() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        String url = buildEndpoint("datastax", "philosophers-quotes", "train", 0, 100);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        HFDatasetPage hfDataSet = JsonUtils.unmarshallBean(response.body(), HFDatasetPage.class);
        System.out.println(hfDataSet.getNumRowsTotal());
    }

}
