create table route_time
(
  id    BIGINT auto_increment primary key,
  route_id int references route (id),
  name varchar(50) not null,
  time_total int,
  time_obk int,
  time_flights int
);

create table route
(
    id       BIGINT auto_increment primary key,
    depo_id int references depo (id),
    number varchar(50) NOT NULL
);

create table depo
(
  id BIGINT auto_increment primary key,
  schedule_id int references schedule (id),
  name varchar(500) NOT NULL

);

create table schedule
(
    id BIGINT auto_increment primary key,
    date date not null unique
);

