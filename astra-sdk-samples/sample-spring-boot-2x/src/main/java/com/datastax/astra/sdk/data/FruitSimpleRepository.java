package com.datastax.astra.sdk.data;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.cassandra.repository.support.MappingCassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Same as Cassandra Repository but with access to {@see CassandraOperations}.
 */
@Repository
public class FruitSimpleRepository extends SimpleCassandraRepository<Fruit, String> {

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
    public FruitSimpleRepository(CqlSession cqlSession, CassandraOperations ops) {
        super(new MappingCassandraEntityInformation<>(
                (CassandraPersistentEntity<Fruit>) ops
                        .getConverter()
                        .getMappingContext()
                        .getRequiredPersistentEntity(Fruit.class), ops.getConverter()),
                ops);
        this.cqlSession = cqlSession;
        this.cassandraTemplate = ops;
    }


}
