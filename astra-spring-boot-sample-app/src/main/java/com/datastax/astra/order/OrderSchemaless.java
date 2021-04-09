package com.datastax.astra.order;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSchemaless {
    
    private UUID    orderId;
    private UUID    productId;
    private Integer productQuantity;
    private String  productName;
    private Float   productPrice;
    private Instant addedToOrderTimestamp;

}
