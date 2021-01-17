package org.datastax.astra.doc;

import static org.datastax.astra.AstraClient.DEFAULT_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.DEFAULT_TIMEOUT;
import static org.datastax.astra.AstraClient.HEADER_CONTENT_TYPE;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.datastax.astra.AstraClient;
/**
 * Client for the Stargate Document REST API.
 * 
            HttpResponse<CollectionMetaDataResponse> response = client.getHttpClient()
                    .send(request, new JsonBodyHandler<>(CollectionMetaDataResponse.class));
            return response.body().getData().stream()
                    .map(CollectionMetaData::getName)
                    .collect(Collectors.toSet());

 * @author Cedrick LUNVEN (@clunven)
 */
public class DocumentApiClient {
    
    /** Constants. */
    private static final String PATH_NAMESPACES  = "/v2/namespaces";
    private static final String PATH_COLLECTIONS = "/collections";
    
    /** Astra Client. */
    private final AstraClient client;
    
    /** Namespace. */
    private final String namespace;
    
    /**
     * Full constructor.
     */
    public DocumentApiClient(AstraClient astraClient, String namespace) {
        this.client    = astraClient;
        this.namespace = namespace;
    }
    
    /**
     * Using current resource GET to evaluate if a document exists.
     *
     * - 200 means the document exists
     * - otherwise it does not. As of now, the API return 204 if not found (it should be 404)
     */
    public boolean exist(String docId, String collectionName) {
        Objects.requireNonNull(collectionName);
        Objects.requireNonNull(docId);
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() 
                            + PATH_NAMESPACES + namespace 
                            + PATH_COLLECTIONS + collectionName
                            + "/" + docId))
                    .GET().build();
            return 200 == client.getHttpClient()
                                .send(request, BodyHandlers.ofString())
                                .statusCode();
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
   
    
    
    /**
     * Using the GET resource to return object. (object mapping leveraging Jackson).
     *
     * @param <B>
     *          current DTO
     * @param collectionName
     *          collection Name
     * @param docId
     *          target document id
     * @param clazz
     *          Dto class to allow dynamic mapping,
     * @return
     */
    public <B extends Serializable> Optional<B> findById(String collectionName, String docId, Class<B> clazz) {
        Objects.requireNonNull(collectionName);
        Objects.requireNonNull(docId);
        Objects.requireNonNull(clazz);
        try {
            
            String uri = client.getBaseUrl()
                    + "/v2/namespaces/" + namespace 
                    + "/collections/" + collectionName
                    + "/" + docId;
            
            // Creating Req
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(uri))
                    .GET().build();
                    
            // Call
            HttpResponse<String> response = client.getHttpClient().send(request, BodyHandlers.ofString());
            
            if (null !=response && response.statusCode() == 200) {
                return Optional.of(client.getObjectMapper().readValue(response.body(), clazz));
            } else if (204 == response.statusCode()) {
                return Optional.empty();
            } else {
                throw new IllegalArgumentException("An error occured: " + response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Leveraging resources DELETE to remove a document, it must exists.
     *
     * @param collectionName
     *          collectionName
     * @param docId
     *          documentId
     *
    public void delete(String docId, String collectionName) {
        if (!exist(docId, collectionName)) {
            throw new IllegalArgumentException("Invalid collectionName/documentId, this object does not exist"); 
        }
        try {
            HttpRequest req = httpRequest().uri(uriFindById(docId, collectionName)).DELETE().build();
            httpClient.send(req, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Leveraging resources PATCH to implement UPSERT.
     * 
     * @param <D>
     *      cuurent DTO
     * @param doc
     *      wrapper for Stargate
     * @return
     *      document id (the one provided in doc)
     *
    public <D extends Serializable> String upsert(StargateDocument<D> doc) {
        Objects.requireNonNull(doc);
        if (doc.getDocumentId().isEmpty()) {
            throw new IllegalArgumentException("Cannot upsert if not documentId is provided.");
        }
        try {
            Builder reqBuilder = httpRequest()
                    .uri(uriCreateNewDocument(doc))
                    .method("PATCH", BodyPublishers.ofString(objectMapper.writeValueAsString(doc.getData())));
            HttpResponse<String> response = httpClient.send(reqBuilder.build(), BodyHandlers.ofString());
            // Patch always return 200
            if (null !=response && HttpStatus.OK.value() == response.statusCode()) {
                return (String) objectMapper.readValue(response.body(), Map.class).get("documentId");
            } else {
                throw new IllegalArgumentException(response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Leveraring the $exist filter operator to get all documents (with an always present property like PK)
     *
     * @param collectionName
     *          collection name for the document
     * @param propertyName
     *          property name of the document
     * @return
     *        set of ids
     *
    public Set<String> findAllDocumentIds(String collectionName, String propertyName) {
        return findIds(uriFindAllIds(collectionName, propertyName));
    }
    
    /**
     * Leveraring the $eq filter operator to get documents matching a criteria
     *
     * @param collectionName
     *          collection name for the document
     * @param propertyName
     *          property name of the document
     * @param propertyValue
     *         expected value      
     * @return
     *        set of ids
     *
    public Set<String> findDocumentsIdsFilterByPropertyValue(String collectionName, String propertyName, String propertyValue) {
        return findIds(uriFindByPropertyValue(collectionName, propertyName, propertyValue));
    }
    
    /**
     * Syntax sugar..
     *
    @SuppressWarnings("unchecked")
    private Set<String> findIds(URI uri) {
        Objects.requireNonNull(uri);
        try {
            HttpResponse<String> response = httpClient.send(httpRequest().uri(uri).GET().build(), BodyHandlers.ofString());
            if (null !=response && HttpStatus.OK.value() == response.statusCode()) {
                Map<String, Object> o = (Map<String, Object>) objectMapper.readValue(response.body(), Map.class).get("data");
                return o.keySet();
            } else {
                throw new IllegalArgumentException(response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Build URL to GET all ids with 'exists' OP.
     * Note that the query need to be ENCODE and all '{' '}' are escaped %2F..
     *
    private URI uriFindAllIds(String colName, String key) {
        Objects.requireNonNull(colName);
        Objects.requireNonNull(key);
        return UriComponentsBuilder.fromUriString(url
                + "/v2/namespaces/" + namespace + "/collections/" + colName)
                .queryParam("where", "{\"" + key + "\": {\"$exists\": true}}")
                .build().encode().toUri();
    }
    
    /**
     * Build URL to GET all ids matching a criteria.
     * Note that the query need to be ENCODE and all '{' '}' are escaped %2F..
     *
    private URI uriFindByPropertyValue(String colName, String propName, String propValue) {
        // Building search Query
        return UriComponentsBuilder.fromUriString(url
                + "/v2/namespaces/" + namespace + "/collections/" + colName)
                .queryParam("where", "{\"" + propName + "\": {\"$eq\": \"" + propValue + "\"}}")
                .build().encode().toUri();
    }
    
    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getNameSpace() {
        return namespace;
    }
}
