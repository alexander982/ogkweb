create table users
(id long primary key,
 first_name varchar(30),
 last_name varchar(30),
 email varchar(30),
 admin boolean,
 last_login time,
 last_ip varchar(15),
 is_active boolean,
 pass varchar(300),
 remember_token varchar(60));