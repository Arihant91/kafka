CREATE KEYSPACE orders
WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
USE orders;

CREATE TABLE orders (
    region_id bigint,
    location_id bigint,
    type_id bigint,
    time_of_scraping TIMESTAMP,
    order_id bigint,
    duration int,
    is_buy_order boolean,
    issued text,
    min_volume int,
    price decimal,
    range text,
    system_id bigint,
    volume_remain bigint,
    volume_total bigint,
    PRIMARY KEY ((region_id), time_of_scraping, location_id, type_id, order_id)
);

CREATE INDEX ON orders (is_buy_order);

CREATE TABLE orders_mean (
    region_id bigint,
    location_id bigint,
    type_id bigint,
    time_of_scraping TIMESTAMP,
    is_buy_orders boolean,
    avg_price decimal,
    highest_price decimal,
    lowest_price decimal,
    order_count int,
    volume_remain bigint,
    PRIMARY KEY ((region_id), time_of_scraping, location_id, type_id)
);

CREATE INDEX ON orders_mean (is_buy_orders);


