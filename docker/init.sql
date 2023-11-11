drop table if exists route;
drop table if exists depo;
drop table if exists schedule;

create table route
(
    id       BIGINT auto_increment primary key,
    number varchar(50) NOT NULL unique
);

create index route_id_index on route(id);
create index route_number_index on route(number);

create table depo
(
  id BIGINT auto_increment primary key,
  name varchar(500) not null unique
);

create index depo_id_index on depo(id);
create index depo_name_index on depo(name);

create table schedule
(
    id BIGINT auto_increment primary key,
    date date not null,
    time varchar(500) not null,
    depo_id int references depo (id),
    route_id int references route (id)
);

create index schedule_date_index on schedule(date);
