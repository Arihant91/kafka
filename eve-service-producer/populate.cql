CREATE KEYSPACE orders
WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
USE orders;

create table orders {
