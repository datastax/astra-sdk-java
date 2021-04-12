CREATE TABLE IF NOT EXISTS starter_orders (
  order_id uuid,
  product_id uuid,
  product_quantity int,
  product_name text,
  product_price decimal,
  added_to_order_at timestamp,
  PRIMARY KEY ((order_id), product_id)
 ) WITH CLUSTERING ORDER BY (product_id DESC);
 
 
 INSERT INTO starter_orders(order_id, product_id, product_quantity, product_name, product_price, added_to_order_at)
 VALUES(uuid(), uuid(), 1, 'iPhone', 699.99, toTimestamp(now()));
 INSERT INTO starter_orders(order_id, product_id, product_quantity, product_name, product_price, added_to_order_at)
 VALUES(uuid(), uuid(), 1, 'iPad', 799.99, toTimestamp(now()));
 INSERT INTO starter_orders(order_id, product_id, product_quantity, product_name, product_price, added_to_order_at)
 VALUES(uuid(), uuid(), 1, 'Macbook', 2399.3, toTimestamp(now()));
 