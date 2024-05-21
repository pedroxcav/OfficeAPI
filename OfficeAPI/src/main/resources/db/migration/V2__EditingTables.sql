alter table projects add constraint unique (name);
alter table projects change status expired boolean not null default false;

alter table teams add constraint unique (name);

alter table tasks add constraint unique (title);
alter table tasks change status expired boolean not null default false;

alter table comments modify column posted_at datetime not null default now();