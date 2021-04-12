package com.datastax.astra.order;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(value = "starter_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = -7527198439031945035L;

    @PrimaryKey
    private OrderPrimaryKey key;

    @Column("product_quantity")
    @CassandraType(type = CassandraType.Name.INT)
    private Integer productQuantity;

    @Column("product_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String productName;

    @CassandraType(type = CassandraType.Name.DECIMAL)
    @Column("product_price")
    private Float productPrice;

    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    @Column("added_to_order_at")
    private Instant addedToOrderTimestamp;

}
