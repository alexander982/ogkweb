--:name get-unit-by-id :? :1
select id, prefix, num, name from unit
where id = :id;

--:name get-units :? :*
select id, prefix, num, name from unit
where prefix like :pref and num like :num and name like :name;

--:name get-composition :? :*
select u.id, c.qnt, c.pos, u.prefix, u.num, u.name from 
contain c inner join unit u on u.id = c.unit_id 
where c.cont_id in (select id from unit
               where prefix like :pref and num like :num)
order by convert(c.pos,int);

--:name get-composition-by-id :? :*
select u.id, c.qnt, c.pos, u.prefix, u.num, u.name, c.code from
contain c inner join unit u on u.id = c.unit_id 
where c.cont_id = :id
order by convert(c.pos,int);

--:name get-includes :? :*
select u.id, u.prefix, u.num, u.name
from contain c inner join unit u on u.id = c.cont_id
where c.unit_id in (select id from unit
               where prefix like :pref and num like :num);

--:name get-occurrence-by-id :? :*
select u.id, u.prefix, u.num, u.name
from contain c inner join unit u on u.id = c.cont_id
where c.unit_id = :id;

--:name update-id-param :!
SET @id_=:id;

--:name get-metals-by-id :? :1
with recursive sostav (pref, num, name, qnt, gold, silver, pl, pal, id) as (
    SELECT u2.prefix, u2.num, u2.name, c.qnt,
        u2.gold, u2.silver, u2.pl, u2.pal, u2.id
    FROM (UNIT u inner join contain c on u.id = c.cont_id)
    inner join unit u2 on u2.id  = c.unit_id
    where u.id = @id_
    union all
    select u.prefix, u.num, u.name, c.qnt, u.gold, u.silver, u.pl, u.pal, u.id
    from ( sostav s inner join contain c on s.id = c.cont_id)
    inner join unit u on c.unit_id = u.id)
select sum(cast(gold as double)*cast(qnt as int)) as gold,
       sum(cast(silver as double)*cast(qnt as int)) as silver,
       sum(cast(pl as double)*cast(qnt as int)) as pl,
       sum(cast(pal as double)*cast(qnt as int)) as pal
from sostav;

--:name get-parts-with-metals :? :*
with recursive sostav (pref, num, name, qnt, gold, silver, pl, pal, id, par_id) as (
    SELECT u2.prefix, u2.num, u2.name, c.qnt,
        u2.gold, u2.silver, u2.pl, u2.pal, u2.id, @id_
    FROM (UNIT u inner join contain c on u.id = c.cont_id)
    inner join unit u2 on u2.id  = c.unit_id
    where u.id = @id_
    union all
    select u.prefix, u.num, u.name, c.qnt, u.gold, u.silver, u.pl, u.pal, u.id, c.cont_id
    from ( sostav s inner join contain c on s.id = c.cont_id)
    inner join unit u on c.unit_id = u.id)
select *
from sostav where cast(gold as double) > 0.0 or
     cast(silver as double) > 0.0 or
     cast(pl as double) > 0.0 or
     cast(pal as double) > 0.0;

--:name get-statement-by-firm :? :*
with recursive sostav (pref, num, name, qnt, code, id, par_id) as (
    SELECT u2.prefix, u2.num, u2.name, c.qnt, c.code,
        u2.id, @id_
    FROM (UNIT u inner join contain c on u.id = c.cont_id)
    inner join unit u2 on u2.id  = c.unit_id
    where u.id = @id_
    union all
    select u.prefix, u.num, u.name, c.qnt, c.code, u.id, c.cont_id
    from ( sostav s inner join contain c on s.id = c.cont_id)
    inner join unit u on c.unit_id = u.id)
select s.pref, s.num, s.name, s.qnt, s.id, s.par_id as node_id, unit.prefix || unit.num as node
from sostav  s inner join unit on par_id = unit.id
where code = 41 and pref like :firm
order by s.pref, s.num, s.name;

--:name get-products :? :*
select cont_id, pref, name, code_prod from product 
where pref like :pref and (name like :name or name is null)

--:name get-diff-by-ids :? :*
select pref1, num1, name1, qnt1, pref2, num2, name2, qnt2, pos1, pos2
from (select u2.prefix as pref1, u2.num as num1, u2.name as name1,
             c.pos as pos1, c.qnt as qnt1
      from (unit u inner join contain c on u.id = c.cont_id)
      inner join unit u2 on c.unit_id = u2.id
           where u.id = :id1
      minus
      select u2.prefix, u2.num, u2.name, c.pos, c.qnt from
      (unit u inner join contain c on u.id = c.cont_id)
      inner join unit u2 on c.unit_id = u2.id
           where u.id = :id2)
left outer join
      (select u2.prefix as pref2, u2.num as num2,
              u2.name as name2, c.pos as pos2, c.qnt as qnt2
       from (unit u inner join contain c on u.id = c.cont_id)
       inner join unit u2 on c.unit_id = u2.id
            where u.id = :id2
       minus
       select u2.prefix, u2.num, u2.name, c.pos, c.qnt from
       (unit u inner join contain c on u.id = c.cont_id)
       inner join unit u2 on c.unit_id = u2.id
            where u.id = :id1)
on pos1 = pos2 union
       select pref1, num1, name1, qnt1, pref2, num2, name2, qnt2, pos1, pos2
       from (select u2.prefix as pref1, u2.num as num1,
                    u2.name as name1, c.pos as pos1, c.qnt as qnt1
       from (unit u inner join contain c on u.id = c.cont_id)
       inner join unit u2 on c.unit_id = u2.id
            where u.id = :id1
       minus
       select u2.prefix, u2.num, u2.name, c.pos, c.qnt from
       (unit u inner join contain c on u.id = c.cont_id)
       inner join unit u2 on c.unit_id = u2.id
            where u.id = :id2)
right outer join
       (select u2.prefix as pref2, u2.num as num2,
               u2.name as name2, c.pos as pos2, c.qnt as qnt2
        from (unit u inner join contain c on u.id = c.cont_id)
        inner join unit u2 on c.unit_id = u2.id
             where u.id = :id2
        minus
        select u2.prefix, u2.num, u2.name, c.pos, c.qnt from
        (unit u inner join contain c on u.id = c.cont_id)
        inner join unit u2 on c.unit_id = u2.id
             where u.id = :id1 )
on pos1 = pos2

--:name get-plan-years :? :*
select distinct year from plan order by year desc;

--:name get-plans :? :*
select unit_id, model, :i:month as qnt from plan
where year = :year and quarter = :quarter and :i:month <> 0
order by kpprod;

--:name get-docs-by-fname :? :*
select * from docs where fname like :fname;

--:name get-doc-by-id :? :1
select * from docs where id = :id;

--:name create-user! :! :n
insert into users
(login, pass, first_name, last_name)
values(:login, :pass, :first-name, :last-name);

--:name get-user :? :1
select * from users where id = :id;

--:name get-users :? :*
select * from users limit :limit offset :offset;

--:name get-user-by-login :? :1
select * from users where login = :login;

--:name get-user-by-token :? :1
select * from users where remember_token = :token;

--:name update-user :! :n
update users set :i:field = :value where id = :id;

--:name update-user-token! :! :n
update users set remember_token = :token where id = :id;

--:name set-admin! :! :n
update users set admin = :value where id = :id;
