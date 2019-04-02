--:name get-all-params :? :*
select * from params;

--:name create-param! :! :n
insert into params
(name, type_, created, updated)
values (:name, :type, default, default);

--:name get-param :? :1
select * from params where id = :id;

--:name update-param! :! :n
update params set name = :name, type_ = :type, updated = default
where id = :id;

--:name delete-param! :! :n
delete from params where id = :id;

--:name get-unit-params :? :*
select * from unit_param inner join params on unit_param.param_id = params.id
where unit_param.unit_id = :id;

--:name add-unit-params! :! :n
insert into unit_param
(unit_id, param_id, value, updated_by)
values (:unit-id, :param-id, :value, :updated-by);

--:name update-unit-param! :! :n
update unit_param set value = :value, updated_by = :updated-by
where unit_id = :unit-id and param_id = :param-id;

--:name delete-unit-param! :! :n
delete from unit_param where unit_id = :unit-id and param_id = :param-id;
