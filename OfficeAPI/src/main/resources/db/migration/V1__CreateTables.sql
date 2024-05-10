create table companies (
    id binary(16) primary key,
    name varchar(50) unique not null,
    cnpj varchar(14) unique not null,
    password varchar(255) not null,
    role tinyint not null
);

create table employees (
    id binary(16) primary key,
    name varchar(50) not null,
    username varchar(50) not null unique,
    cpf varchar(11) unique not null,
    email varchar(50) unique not null,
    password varchar(255) not null,
    role tinyint not null,
    team_id bigint,
    company_id binary(16) not null
);

create table adresses (
    id bigint auto_increment primary key,
    zip_code varchar(8) not null,
    number varchar(10) not null,
    street varchar(50) not null,
    neighborhood varchar(50) not null,
    city varchar(50) not null,
    state varchar(50) not null,
    company_id binary(16) not null unique
);

create table projects (
    id bigint auto_increment primary key,
    name varchar(50) not null,
    description varchar(200) not null,
    deadline date not null,
    status boolean not null,
    company_id binary(16) not null,
    manager_id binary(16) unique
);

create table teams (
    id bigint auto_increment primary key,
    name varchar(50) not null,
    company_id binary(16) not null,
    project_id bigint not null
);

create table tasks (
    id bigint auto_increment primary key,
    title varchar(50) not null,
    description varchar(200) not null,
    deadline datetime not null,
    status boolean not null,
    project_id bigint not null
);

create table comments (
    id bigint auto_increment primary key,
    content varchar(500) not null,
    owner_id binary(16) not null,
    task_id bigint not null,
    posted_at datetime not null
);

alter table employees add foreign key (team_id) references teams (id) on delete set null on update cascade;
alter table employees add foreign key (company_id) references companies (id) on delete cascade;

alter table adresses add foreign key (company_id) references companies (id) on delete cascade on update cascade;

alter table projects add foreign key (company_id) references companies (id) on delete cascade on update cascade;
alter table projects add foreign key (manager_id) references employees (id) on delete set null on update cascade;

alter table teams add foreign key (company_id) references companies (id) on delete cascade on update cascade;
alter table teams add foreign key (project_id) references projects (id) on delete cascade on update cascade;

alter table tasks add foreign key (project_id) references projects (id) on delete cascade on update cascade;

alter table comments add FOREIGN KEY (owner_id) references employees (id) on delete cascade on update cascade;
alter table  comments add foreign key (task_id) references tasks (id) on delete cascade on update cascade;