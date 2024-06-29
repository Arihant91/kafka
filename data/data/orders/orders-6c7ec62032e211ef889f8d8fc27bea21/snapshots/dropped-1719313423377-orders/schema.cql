CREATE TABLE IF NOT EXISTS orders.orders (
    region_id int,
    location_id bigint,
    type_id bigint,
    time_of_scraping timestamp,
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
    PRIMARY KEY ((region_id, location_id, type_id), time_of_scraping, order_id)
) WITH ID = 6c7ec620-32e2-11ef-889f-8d8fc27bea21
    AND CLUSTERING ORDER BY (time_of_scraping ASC, order_id ASC)
    AND additional_write_policy = '99p'
    AND allow_auto_snapshot = true
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND cdc = false
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '16', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND memtable = 'default'
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND extensions = {}
    AND gc_grace_seconds = 864000
    AND incremental_backups = true
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99p';
CREATE INDEX IF NOT EXISTS orders_is_buy_order_idx ON orders.orders (is_buy_order);
