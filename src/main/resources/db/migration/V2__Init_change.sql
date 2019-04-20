create table record
(
	id bigserial not null,
	value varchar(100)
);

create unique index record_id_uindex
	on record (id);

alter table record
	add constraint record_pk
		primary key (id);

