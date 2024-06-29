CREATE MATERIALIZED VIEW IF NOT EXISTS orders.orders_mean_by_region_type AS
    SELECT region_id, type_id, time_of_scraping, location_id, avg_price, highest_price, is_buy_orders, lowest_price, order_count, volume_remain
    FROM orders.orders_mean
    WHERE region_id IS NOT NULL AND type_id IS NOT NULL AND time_of_scraping IS NOT NULL AND location_id IS NOT NULL
    PRIMARY KEY ((region_id, type_id), time_of_scraping, location_id)
 WITH ID = c80254e0-32ff-11ef-889f-8d8fc27bea21
    AND CLUSTERING ORDER BY (time_of_scraping ASC, location_id ASC)
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
    AND extensions = {}
    AND gc_grace_seconds = 864000
    AND incremental_backups = true
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99p';
