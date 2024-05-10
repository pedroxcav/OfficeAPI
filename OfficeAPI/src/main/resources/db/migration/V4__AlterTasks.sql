alter table tasks add constraint unique (title);
alter table tasks change status active boolean not null;