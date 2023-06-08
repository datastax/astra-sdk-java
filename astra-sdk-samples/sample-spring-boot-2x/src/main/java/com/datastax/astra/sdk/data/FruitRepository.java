package com.datastax.astra.sdk.data;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Repository
 */
@Repository
public interface FruitRepository extends CassandraRepository<Fruit, String> {}
