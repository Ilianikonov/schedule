drop table if exists route_time;
drop table if exists route;
drop table if exists depo;
drop table if exists schedule;

create table schedule
(
    id BIGINT auto_increment primary key,
    date date not null unique
);
create table depo
(
    id BIGINT auto_increment primary key,
    schedule_id int references schedule (id),
    name varchar(500) NOT NULL

);
create table route
(
    id       BIGINT auto_increment primary key,
    depo_id int references depo (id),
    number varchar(50) NOT NULL
);
create table route_time
(
    id    BIGINT auto_increment primary key,
    route_id int references route (id),
    name varchar(50) not null,
    time_total int,
    time_obk int,
    time_flights int
);

create index schedule_date_index on schedule(date);
create index depo_id_index on depo(id);
create index depo_name_index on depo(name);
create index route_id_index on route(id);
create index route_number_index on route(number);
create index route_time_id_index on route_time(id);
create index route_time_name_index on route_time(name);

