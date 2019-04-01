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
