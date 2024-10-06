CREATE KEYSPACE orders
WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
USE orders;

CREATE TABLE orders_stats_by_region (
    region_id bigint,
    location_id bigint,
    type_id bigint,
    time_of_scraping TIMESTAMP,
    is_buy_orders boolean,
    avg_price decimal,
    median_price decimal,
    highest_price decimal,
    lowest_price decimal,
    order_count int,
    volume_remain bigint,
    PRIMARY KEY ((region_id), time_of_scraping, type_id)
);

CREATE INDEX ON orders_stats_by_region (is_buy_orders);


