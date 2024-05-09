alter table projects add constraint unique (name);
alter table projects change status active boolean not null;