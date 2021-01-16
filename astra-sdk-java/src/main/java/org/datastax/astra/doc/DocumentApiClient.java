package org.datastax.astra.doc;

import static org.datastax.astra.AstraClient.HEADER_CONTENT_TYPE;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.datastax.astra.AstraClient;
import org.datastax.astra.doc.dto.ApiResponse;
import org.datastax.astra.doc.dto.CollectionMetaData;
import org.datastax.astra.utils.MappingUtils;

import com.fasterxml.jackson.core.type.TypeReference;
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
    
    private static final String PATH_NAMESPACES = "/v2/namespaces/";
    private static final String PATH_COLLECTIONS = "/collections/";
    
    /** Astra Client. */
    private final AstraClient client;
    
    /** Namespace. */
    private final String namespace;
    
    /** Constants. */
    private static final Duration DEFAULT_TIMEOUT      = Duration.ofSeconds(20);
    private static final String   DEFAULT_CONTENT_TYPE =  "application/json";
    
    /**
     * Full constructor.
     */
    public DocumentApiClient(AstraClient astraClient, String namespace) {
        this.client    = astraClient;
        this.namespace = namespace;
    }
    
    /**
     * List collections in namespace.
     * 
     * GET /v2/namespaces/{namespace-id}/collections
     */
    public Set<String> findAllCollections() {
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_NAMESPACES + namespace + PATH_COLLECTIONS))
                    .GET().build();
            
            // Invoke
            HttpResponse<String> response = 
                    client.getHttpClient().send(request, BodyHandlers.ofString());
            
            // Marshalling as Object
            ApiResponse<List<CollectionMetaData>> oResponse = 
                    client.getObjectMapper().readValue(response.body(), 
                            new TypeReference<ApiResponse<List<CollectionMetaData>>>(){});
            
            // Mapping to set
            return oResponse.getData().stream()
                         .map(CollectionMetaData::getName)
                         .collect(Collectors.toSet());
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    } 
    
    public boolean existCollection(String collectionName) {
        Objects.requireNonNull(collectionName);
        return findAllCollections().contains(collectionName);
    }
    
    public void createCollection(String collectionName) {
        try {
            
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_NAMESPACES + namespace + PATH_COLLECTIONS))
                    .POST(BodyPublishers.ofString("{\"name\":\"" + collectionName + "\"}"))
                    .build();
            
            // Invoke
            HttpResponse<String> response = 
                    client.getHttpClient().send(request, BodyHandlers.ofString());
            
            if (HttpURLConnection.HTTP_CONFLICT != response.statusCode()) {
                throw new IllegalStateException("Collection already exists '" + response.statusCode() + "' :" + response.body());
            }
            
            if (HttpURLConnection.HTTP_INTERNAL_ERROR != response.statusCode()) {
                throw new IllegalStateException("An error occured: " + response.body()); 
            }
            
            if (HttpURLConnection.HTTP_GATEWAY_TIMEOUT == response.statusCode()) {
                System.out.println("Error but might be created already");
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    }
    
    /**
     * Mapping from ClassName to doc.
     */
    public boolean existDocument(Class<?> bean, String docId) {
        return existDocument(MappingUtils.mapToCollection(bean), docId);
    }
    
    /**
     * Using current resource GET to evaluate if a document exists.
     *
     * - 200 means the document exists
     * - otherwise it does not. As of now, the API return 204 if not found (it should be 404)
     */
    public boolean existDocument(String collectionName, String documentId) {
        Objects.requireNonNull(collectionName);
        Objects.requireNonNull(documentId);
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() 
                            + PATH_NAMESPACES + namespace 
                            + PATH_COLLECTIONS + collectionName
                            + "/" + documentId))
                    .GET().build();
            return 200 == client.getHttpClient()
                                .send(request, BodyHandlers.ofString())
                                .statusCode();
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    /**
     * Using PUT and POST to create document (not PATCH here)
     * - PUT if a documentId is communicated
     * - POST if not
     * 
     * One return 200 and the other 201, explain the OR statement.
     * 
     * @param <D>
     *      serializable object to convert as JSON  
     * @param authToken
     *      authentication token required in headers
     * @param doc
     *      document to be serialized (using Jackson)
     * @param docId
     *      optional unique identifier for the document
     * @param collectionName
     *      collection name
     * @return
     *      return document Id (the one provided eventually)
     */
    
    public <D extends Serializable> String create(D doc) {
        return create(doc, MappingUtils.mapToCollection(doc.getClass()), Optional.empty());
    }
    public <D extends Serializable> String create(D doc, String docId) {
        return create(doc, MappingUtils.mapToCollection(doc.getClass()), Optional.ofNullable(docId));
    }
    public <D extends Serializable> String create(D doc, String collectionName, Optional<String> docId) {
        Objects.requireNonNull(doc);
        try {
            
            String uri = client.getBaseUrl()
                    + "/v2/namespaces/" + namespace 
                    + "/collections/" + collectionName
                    + "/";
            if (docId.isPresent()) {
                uri = uri + docId.get();
            }
            
            // Creating Req
            Builder reqBuilder = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(uri));

            
            String reqBody = client.getObjectMapper().writeValueAsString(doc);
            
            // PUT to create a new enforcing Id,POST to create a new with no id 
            if (docId.isEmpty()) {
                reqBuilder.POST(BodyPublishers.ofString(reqBody));
            } else {
                // An Id has been provided, we want to raise an error if already exist
                reqBuilder.PUT(BodyPublishers.ofString(reqBody));
            }
            
            // Call
            HttpResponse<String> response = client.getHttpClient().send(reqBuilder.build(), BodyHandlers.ofString());
            if (null !=response && (response.statusCode() == 201 || response.statusCode() == 200)) {
                return (String) client.getObjectMapper().readValue(response.body(), Map.class).get("documentId");
            } else {
                throw new IllegalArgumentException(response.body());
            }
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
    public <B extends AstraDocument<?>> Optional<B> findById(String collectionName, String docId, Class<B> clazz) {
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
     */
    public void delete(String collectionName, String docId) {
        if (!exists(collectionName, docId)) {
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
     */
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
     */
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
     */
    public Set<String> findDocumentsIdsFilterByPropertyValue(String collectionName, String propertyName, String propertyValue) {
        return findIds(uriFindByPropertyValue(collectionName, propertyName, propertyValue));
    }
    
    /**
     * Syntax sugar..
     */
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
     */
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
     */
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
