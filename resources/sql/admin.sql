--:name backup-database :? :*
script nosettings drop
to 'backup.sql' table users, cversion, versions, params, unit_param;

--:name get-all-params :? :*
select * from params;

--:name get-values :? :*
select p.name as param, u.prefix, u.num, u.name, v.value, v.updated, us.login
from ((users us inner join unit_param v on v.updated_by = us.id)
 inner join params p on p.id = v.param_id)
 inner join unit u on u.id = v.unit_id
order by v.updated
limit 50;