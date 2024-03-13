create table if not exists params
(id long identity primary key,
 name varchar(100) not null,
 type_ char(1) not null default 't',
 created timestamp default current_timestamp,
 updated timestamp default current_timestamp,
 constraint u_name unique(name),
 constraint c_type check (type_ = 't' or type_ = 'n'));
