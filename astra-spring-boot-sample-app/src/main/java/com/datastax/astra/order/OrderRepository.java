package com.datastax.astra.order;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveCassandraRepository<Order, OrderPrimaryKey> {
    
}