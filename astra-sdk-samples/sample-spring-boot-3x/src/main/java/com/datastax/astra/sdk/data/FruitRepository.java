package com.datastax.astra.sdk.data;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Repository
 */
@Repository
public interface FruitRepository extends CassandraRepository<Fruit, String> {}
