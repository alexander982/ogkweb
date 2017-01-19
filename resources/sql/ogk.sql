--:name get-unit-by-id :? :1
select id, prefix, num, name from unit
where id = :id;

--:name get-units :? :*
select id, prefix, num, name from unit
where prefix like :pref and num like :num and name like :name;

--:name get-composition-by-id :? :*
select u2.id, c.qnt, c.pos, u2.prefix, u2.num, u2.name from 
(unit u inner join contain c on u.id = c.cont_id) inner join unit u2 on c.unit_id = u2.id 
where u.id = :id
order by c.pos;

--:name get-occurrence-by-id :? :*
select u2.id, u2.prefix, u2.num, u2.name 
from (unit u inner join contain c on u.id = c.unit_id) inner join unit u2 on c.cont_id = u2.id 
where u.id = :id;

--:name get-metals-by-id :? :1
with recursive sostav (pref, num, name, qnt, gold, silver, pl, pal, id) as (
    SELECT u2.prefix, u2.num, u2.name, c.qnt,
        u2.gold, u2.silver, u2.pl, u2.pal, u2.id
    FROM (UNIT u inner join contain c on u.id = c.cont_id)
    inner join unit u2 on u2.id  = c.unit_id
    where u.id = :id
    union all
    select u.prefix, u.num, u.name, c.qnt, u.gold, u.silver, u.pl, u.pal, u.id
    from ( sostav s inner join contain c on s.id = c.cont_id)
    inner join unit u on c.unit_id = u.id)
select sum(cast(gold as double)*cast(qnt as int)) as gold,
       sum(cast(silver as double)*cast(qnt as int)) as silver,
       sum(cast(pl as double)*cast(qnt as int)) as pl,
       sum(cast(pal as double)*cast(qnt as int)) as pal
from sostav;

--:name get-metals-by-pref-num :? :1
with recursive sostav (pref, num, name, qnt, gold, silver, pl, pal, id) as (
    SELECT u2.prefix, u2.num, u2.name, c.qnt,
        u2.gold, u2.silver, u2.pl, u2.pal, u2.id
    FROM (UNIT u inner join contain c on u.id = c.cont_id )
    inner join unit u2 on u2.id  = c.unit_id
    where u.id  =  (select id from unit
        where prefix like :pref and num like :num)
    union all
    select u.prefix, u.num, u.name, c.qnt, u.gold, u.silver, u.pl, u.pal, u.id
    from ( sostav s inner join contain c on s.id = c.cont_id)
    inner join unit u on c.unit_id = u.id)
select sum(cast(gold as double)*cast(qnt as int)) as gold,
       sum(cast(silver as double)*cast(qnt as int)) as silver,
       sum(cast(pl as double)*cast(qnt as int)) as pl,
       sum(cast(pal as double)*cast(qnt as int)) as pal
from sostav;

--:name get-products :? :*
select cont_id, pref, name from product 
where pref like :pref and (name like :name or name is null)
