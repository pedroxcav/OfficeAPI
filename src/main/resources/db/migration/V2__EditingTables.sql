alter table projects add unique (name);
alter table projects rename column status to expired;

alter table teams add unique (name);

alter table tasks add unique (title);
alter table tasks rename column status to expired;

alter table comments alter column posted_at set default now();