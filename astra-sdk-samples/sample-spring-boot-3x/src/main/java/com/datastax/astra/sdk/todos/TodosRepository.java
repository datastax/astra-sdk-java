package com.datastax.astra.sdk.todos;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Repository.
 */
@Repository
public interface TodosRepository extends CassandraRepository<Todos, UUID> {}
