--:name get-last-db-update :? :1
SELECT v_date FROM versions
WHERE v_id = (SELECT cv_id from cversion);

--:name get-updates :? :*
select * from versions
order by v_date desc
limit 10;

--:name get-materials :? :*
select am.class_, am.name, am.article, am.flow, mu.short_name from
meas_units mu inner join aux_materials am on mu.id = am.meas_unit_id
where unit_id = :id
order by class_;
