CREATE KEYSPACE orders
WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
USE orders;

CREATE TABLE orders_stats_by_region (
    region_id bigint,
    type_id bigint,
    time_of_scraping TIMESTAMP,
    is_buy_orders boolean,
    avg_price decimal,
    median_price decimal,
    std_deviation decimal,
    highest_price decimal,
    lowest_price decimal,
    order_count int,
    volume_remain bigint,
    PRIMARY KEY ((region_id), type_id, time_of_scraping)
);

CREATE TABLE orders_stats_by_location (
    region_id bigint,
    location_id bigint,
    type_id bigint,
    time_of_scraping TIMESTAMP,
    is_buy_orders boolean,
    avg_price decimal,
    median_price decimal,
    std_deviation decimal,
    highest_price decimal,
    lowest_price decimal,
    order_count int,
    volume_remain bigint,
    PRIMARY KEY ((location_id), type_id, time_of_scraping)
);

CREATE TYPE structures (
    id bigint,
    name text
);

CREATE TABLE structures_by_region (
    region_id bigint PRIMARY KEY,
    structures list<frozen<structures>>
);

CREATE INDEX ON orders_stats_by_region (is_buy_orders);
CREATE INDEX ON orders_stats_by_location (is_buy_orders);


