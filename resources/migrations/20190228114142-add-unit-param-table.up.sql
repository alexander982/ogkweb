create table if not exists unit_param
(unit_id integer not null,
 param_id integer not null,
 value varchar(256),
 created timestamp not null default current_timestamp,
 updated timestamp not null default current_timestamp,
 updated_by integer,
 constraint pk_unit_param primary key (unit_id, param_id),
 constraint fk_param_id foreign key (param_id) references params(id)
   on delete restrict on update cascade,
 constraint fk_updated_by foreign key (updated_by) references users(id)
   on delete set null on update cascade);