drop database if exists comics;
create database comics;
use comics;

create table genres (
    id int(11) not null auto_increment,
    genre varchar(45) not null unique,
    description varchar(512) not null,
    primary key (id),
    unique key id_UNIQUE (id)
)  engine=innodb default charset=utf8;

create table roles (
    id int(11) not null auto_increment,
    role varchar(45) not null unique,
    description varchar(255) not null,
    primary key (id),
    unique key id_UNIQUE (id)
)  engine=innodb default charset=utf8;

create table authors (
    id int(11) not null auto_increment,
    name varchar(255) not null unique,
    team bit(1) not null default 0,
    primary key (id),
    unique key id_UNIQUE (id)
)  engine=innodb default charset=utf8;

create table publishers (
    id int(11) not null auto_increment,
    name varchar(255) not null unique,
    notes varchar(4000) null,
    primary key (id),
    unique key id_UNIQUE (id)
)  engine=innodb default charset=utf8;

create table series (
    id int(11) not null auto_increment,
    title varchar(255) not null unique,
    idpublisher int(11) not null,
    ongoing bit(1) not null default 1,
    trimmed bit(1) not null default 0,
    start_year year not null,
    end_year year null,
    primary key (id),
    unique key id_UNIQUE (id),
    key fk_series_publishers (idpublisher),
    constraint fk_series_publishers foreign key (idpublisher)
        references series (id)
        on delete no action on update cascade
)  engine=innodb default charset=utf8;

create table volumes (
    id int(11) not null auto_increment,
    idseries int(11) not null,
    number int(11) not null,
    release_date date not null,
    primary key (id),
    unique key id_UNIQUE (id),
    key fk_volumes_series (idseries),
    constraint fk_volumes_series foreign key (idseries)
        references series (id)
        on delete cascade on update cascade
)  engine=innodb default charset=utf8;

create table series_genres (
    id int(11) not null auto_increment,
    idseries int(11) not null,
    idgenre int(11) not null,
    primary key (id),
    unique key id_UNIQUE (id),
    key fk_series_genres_series (idseries),
    key fk_series_genres_genres (idgenre),
    constraint fk_series_genres_series foreign key (idseries)
        references series (id)
        on delete cascade on update cascade,
    constraint fk_series_genres_genres foreign key (idgenre)
        references genres (id)
        on delete cascade on update cascade
)  engine=innodb default charset=utf8;

create table authors_roles_series (
    id int(11) not null auto_increment,
    idauthor int(11) not null,
    idrole int(11) not null,
    idseries int(11) not null,
    primary key (id),
    unique key id_UNIQUE (id),
    key fk_authors_roles_series_authors (idauthor),
    key fk_authors_roles_series_roles (idrole),
    key fk_authors_roles_series_series (idseries),
    constraint fk_authors_roles_series_authors foreign key (idauthor)
        references authors (id)
        on delete cascade on update cascade,
    constraint fk_authors_roles_series_roles foreign key (idrole)
        references roles (id)
        on delete cascade on update cascade,
    constraint fk_authors_roles_series_series foreign key (idseries)
        references series (id)
        on delete cascade on update cascade
)  engine=innodb default charset=utf8;
