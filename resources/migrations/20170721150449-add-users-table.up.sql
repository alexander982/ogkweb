create table users
(id long identity primary key,
 login varchar(30) not null unique,
 first_name varchar(30),
 last_name varchar(30),
 email varchar(30),
 admin boolean default false,
 last_login time,
 last_ip varchar(15),
 is_active boolean default true,
 pass varchar(300),
 remember_token varchar(60));