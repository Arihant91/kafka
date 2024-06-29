CREATE TABLE IF NOT EXISTS orders.orders (
    location_id bigint,
    time_of_scraping text,
    type_id bigint,
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
    PRIMARY KEY ((location_id, time_of_scraping, type_id), order_id)
) WITH ID = 8158e290-3177-11ef-985d-47c76ac3328f
    AND CLUSTERING ORDER BY (order_id ASC)
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
