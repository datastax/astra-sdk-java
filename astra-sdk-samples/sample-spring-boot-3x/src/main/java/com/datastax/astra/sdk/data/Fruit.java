package com.datastax.astra.sdk.data;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

/**
 * Fruit entity
 */
@Table
public class Fruit implements Serializable {

    /** name. */
    @PrimaryKey
    @CassandraType(type = CassandraType.Name.TEXT)
    private String name;

    /** price. */
    private double price;

    /**
     * Default constructor
     */
    public Fruit() {
    }

    /**
     * Create fruit.
     *
     * @param name
     *      name
     * @param price
     *      prices
     */
    public Fruit(String name, double price) {
        this.name = name;
        this.price = price;
    }

    /**
     * Gets name
     *
     * @return value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set value for name
     *
     * @param name
     *         new value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets price
     *
     * @return value of price
     */
    public double getPrice() {

        return price;
    }

    /**
     * Set value for price
     *
     * @param price
     *         new value for price
     */
    public void setPrice(double price) {
        this.price = price;
    }
}
