package com.dtsx.astra.sdk.langchain;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import static com.dtsx.astra.sdk.cassio.ClusteredCassandraTable.Record;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.dtsx.astra.sdk.cassio.ClusteredCassandraTable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ChatMemoryStore} using Astra DB Vector Search.
 *
 * <ul>
 *  <li>Table contains all chats. (default name is message_store).</li>
 *  <li>Each chat with multiple messages is a partition.</li>
 *  <li>Message id is a time uuid</li>
 *</p>
 *
 * Astra Table looks like
 * <code></code>
 *
 *
 *
 * @see <a href="https://docs.datastax.com/en/astra-serverless/docs/vector-search/overview.html">Astra Vector Store Documentation</a>
 * @author @clunven
 * @since 0.22.0
 */
@Slf4j
public class AstraDbChatMemoryStore implements ChatMemoryStore {

    /**
     * Default message store.
     */
    public static final String DEFAULT_TABLE_NAME = "message_store";

    /**
     * Message Table.
     */
    private final ClusteredCassandraTable messageTable;

    /** Object Mapper. */
    private static final ObjectMapper OM = new ObjectMapper();

    /**
     * Constructor with default table name.
     *
     * @param token
     *      token
     * @param dbId
     *      database idendifier
     * @param dbRegion
     *      database region
     * @param keyspaceName
     *      keyspace name
     */
    public AstraDbChatMemoryStore(String token, String dbId, String dbRegion, String keyspaceName) {
        this(token, dbId, dbRegion, keyspaceName, DEFAULT_TABLE_NAME);
    }

    /**
     * Constructor with default table name.
     *
     * @param token        token
     * @param dbId         database identifier
     * @param dbRegion     database region
     * @param keyspaceName keyspace name
     * @param tableName    table name
     */
    public AstraDbChatMemoryStore(String token, String dbId, String dbRegion, String keyspaceName, String tableName) {
        this(AstraClient.builder()
                .withToken(token)
                .withCqlKeyspace(keyspaceName)
                .withDatabaseId(dbId)
                .withDatabaseRegion(dbRegion)
                .enableCql()
                .enableDownloadSecureConnectBundle()
                .build().cqlSession(), keyspaceName, tableName);
    }

    /**
     * Constructor for message store
     *
     * @param session
     *      cassandra session
     * @param keyspaceName
     *      keyspace name
     * @param tableName
     *      table name
     */
    public AstraDbChatMemoryStore(CqlSession session, String keyspaceName, String tableName) {
        messageTable = new ClusteredCassandraTable(session, keyspaceName, tableName);
    }

    /**
     * Constructor for message store
     *
     * @param session
     *      cassandra session
     * @param keyspaceName
     *      keyspace name
     */
    public AstraDbChatMemoryStore(CqlSession session, String keyspaceName) {
        messageTable = new ClusteredCassandraTable(session, keyspaceName, DEFAULT_TABLE_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public List<ChatMessage> getMessages(@NonNull Object memoryId) {
        return messageTable
                .findPartition(getMemoryId(memoryId))
                .stream()
                .map(this::toChatMessage)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public void updateMessages(@NonNull  Object memoryId, @NonNull List<ChatMessage> list) {
        deleteMessages(memoryId);
        messageTable.upsertPartition(list.stream()
                .map(r -> this.fromChatMessage(getMemoryId(memoryId), r))
                .collect(Collectors.toList()));
    }

    @Override
    public void deleteMessages(@NonNull  Object memoryId) {
        messageTable.deletePartition(getMemoryId(memoryId));
    }

    @Data
    public static class MessageBody {
        private String type;
        private MessageData data;
    }

    @Data
    public static class MessageData {
        private String content;

        @JsonProperty("additional_kwargs")
        private Map<String, String> additionalKwargs = new HashMap<>();

        private boolean example = false;
    }

    private ChatMessage toChatMessage(@NonNull Record record) {
        try {
            MessageBody body = OM.readValue(record.getBody(), MessageBody.class);
            String content = body.getData().getContent();
            switch (body.type) {
                case "system":
                    return SystemMessage.from(content);
                case "ai":
                    return AiMessage.from(content);
                case "user":
                case "human":
                    return UserMessage.from(content);
                default:
                    log.error("Unknown message type {}", body.type);
            }
        } catch(Exception e) {
            log.error("Unable to parse message body", e);
        }
        throw new IllegalArgumentException("Unable to parse message body");
    }

    private Record fromChatMessage(@NonNull String memoryId, @NonNull ChatMessage chatMessage) {
        try {
            Record record = new Record();
            record.setRowId(Uuids.timeBased());
            record.setPartitionId(memoryId);

            MessageBody body = new MessageBody();
            body.setType(chatMessage.type().name().toLowerCase());
            MessageData data = new MessageData();
            data.setContent(chatMessage.text());
            body.setData(data);
            record.setBody(OM.writeValueAsString(body));
            return record;
        } catch(Exception e) {
            log.error("Unable to parse message body", e);
        }
        return null;
    }

    private String getMemoryId(Object memoryId) {
        if (!(memoryId instanceof String) ) {
            throw new IllegalArgumentException("memoryId must be a String");
        }
        return (String) memoryId;
    }


}
