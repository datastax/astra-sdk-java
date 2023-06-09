package com.datastax.astra.sdk.todos;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.cassandra.repository.support.MappingCassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Same as Cassandra Repository but with access to {@see CassandraOperations}.
 */
@Repository
public class TodosSimpleRepository extends SimpleCassandraRepository<Todos, UUID> {

    /**
     * Inject the cassandra connection
     */
    protected final CqlSession cqlSession;

    /**
     * Inject the cassandra template
     */
    protected final CassandraOperations cassandraTemplate;

    /**
     * Mapping to the table.
     *
     * @param cqlSession
     *      current Session
     * @param ops
     *      current Cassandra template
     */
    @SuppressWarnings("unchecked")
    public TodosSimpleRepository(CqlSession cqlSession, CassandraOperations ops) {
        super(new MappingCassandraEntityInformation<>(
                (CassandraPersistentEntity<Todos>) ops
                        .getConverter()
                        .getMappingContext()
                        .getRequiredPersistentEntity(Todos.class), ops.getConverter()),
                ops);
        this.cqlSession = cqlSession;
        this.cassandraTemplate = ops;
    }


}
