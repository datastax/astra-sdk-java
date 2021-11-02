package com.datastax.tutorial;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TodosRepository extends CassandraRepository<Todos, String> {

}
