use dataflow;

CREATE TABLE IF NOT EXISTS TAKARA_STAGE
(
   id int,
   first_name varchar(50),
   last_name varchar(50),
   minutes int,
   data_usage int
);

CREATE TABLE IF NOT EXISTS TAKARA_FINAL
(
   id int,
   first_name varchar(50),
   last_name varchar(50),
   minutes int,
   data_usage int
);
