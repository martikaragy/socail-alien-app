CREATE DATABASE IF NOT EXISTS alien_network;
USE alien_network;

create or replace table planets
(
    id            int auto_increment
        primary key,
    name          varchar(50)                             not null,
    deletedStatus varchar(50) default 'ACTIVE' not null,
    dateCreated   timestamp   default current_timestamp(),
    dateDeleted   timestamp                               null,
    comment       varchar(50)                             null
);

create or replace table cities
(
    id            int auto_increment
        primary key,
    name          varchar(50)                             not null,
    planet     int                                     null,
    deletedStatus varchar(50) default 'ACTIVE',
    dateCreated   timestamp   default current_timestamp(),
    dateDeleted   timestamp                               null,
    comment       varchar(50)                             null,
    constraint cities_planets_id_fk
        foreign key (planet) references planets (id)
);

create or replace table users
(
    id              int auto_increment
        primary key,
    username        varchar(50)                           not null,
    password        varchar(500)                           not null,
    enabled         tinyint(1)                            not null default '1',
    firstName       varchar(50)                           null,
    lastName        varchar(50)                           null,
    email           varchar(50)                           not null,
    address         varchar(50)                           null,
    birthDate       date                                  null,
    cityOfBirth     int                                   null,
    cityOfResidence int                                   null,
    job             varchar(50)                           null,
    education       varchar(50)                           null,
    profilePhoto    varchar(200)                           null,
    coverPhoto      varchar(200)                           null,
    dateCreated     timestamp default current_timestamp(),
    dateDeleted     timestamp                             null,
    deletedStatus   varchar(50) default 'ACTIVE',
    comment         text                                  null,
    constraint users_username_uindex
        unique (username),
    constraint users_cities_id_fk
        foreign key (cityOfBirth) references cities (id),
    constraint users_cities_id_fk_2
        foreign key (cityOfResidence) references cities (id)
);


create or replace table authorities
(   id int auto_increment primary key,
    username  varchar(50) not null,
    authority varchar(50) not null,
    deletedStatus varchar(50) default 'ACTIVE',
    dateCreated   timestamp   default current_timestamp(),
    dateDeleted   timestamp                               null,
    comment       varchar(50)                             null,
    userId int not null,
    constraint authorities_users_userId_fk
        foreign key (userId) references users (id),
    unique (username,authority)
);

create or replace table connections
(
    id               int auto_increment
        primary key,
    senderUser_id    int                                     not null,
    receiverUser_id   int                                     not null,
    connectionStatus varchar(50)                             null,
    deletedStatus    varchar(50) default 'ACTIVE'            ,
    dateCreated      timestamp   default current_timestamp() ,
    dateDeleted      timestamp                               null,
    comment          varchar(50)                             null,
    constraint connections_users_id_fk
        foreign key (receiverUser_id) references users (id),
    constraint connections_users_id_fk_2
        foreign key (senderUser_id) references users (id)
);

create or replace table posts
(
    id            int auto_increment
        primary key,
    value         text                                    not null,
    deletedStatus varchar(50) default 'ACTIVE'            ,
    privacy varchar(10) default 'PUBLIC' not null ,
    dateCreated timestamp   default current_timestamp() ,
    dateDeleted   timestamp                               null,
    user_id       int                                     not null,
    comment       varchar(50)                             null,
    constraint posts_users_id_fk
        foreign key (user_id) references users (id)
);



create or replace table comments
(
    id            int auto_increment
        primary key,
    value         text                                    not null,
    user_id       int                                     not null,
    post_id       int                                     not null,
    deletedStatus varchar(50) default 'ACTIVE'            ,
    dateCreated   timestamp   default current_timestamp() ,
    dateDeleted   timestamp                               null,
    comment       varchar(50)                             null,
    constraint comments_posts_id_fk
        foreign key (post_id) references posts (id),
    constraint comments_users_id_fk
        foreign key (user_id) references users (id)
);

create or replace table likes
(
    id            int auto_increment
        primary key,
    user_id       int                                     not null,
    post_id       int                                     not null,
    deletedStatus varchar(50) default 'ACTIVE'            ,
    dateCreated   timestamp   default current_timestamp() ,
    dateDeleted   timestamp                               null,
    comment       varchar(50)                             null,
    constraint likes_posts_id_fk
        foreign key (post_id) references posts (id),
    constraint likes_users_id_fk
        foreign key (user_id) references users (id)
);

create or replace table posts_comments
(
    post_id    int not null,
    comment_id int not null,
    constraint post_comments_comments_id_fk
        foreign key (comment_id) references comments (id),
    constraint post_comments_posts_id_fk
        foreign key (post_id) references posts (id)
);

create or replace table posts_likes
(
    post_id int not null,
    like_id int not null,
    constraint posts_likes_likes_id_fk
        foreign key (like_id) references likes (id),
    constraint posts_likes_posts_id_fk
        foreign key (post_id) references posts (id)
);

create or replace table visibility
(
    id              int auto_increment
        primary key,
    userId          int                     not null,
    username        tinyint(1)  default '1' not null,
    firstName       tinyint(1)  default '1' not null,
    lastName        tinyint(1)  default '1' not null,
    email           tinyint(1)  default '1' not null,
    address         tinyint(1)  default '1' not null,
    birthDate       tinyint(1)  default '1' not null,
    cityOfBirth     tinyint(1)  default '1' not null,
    cityOfResidence tinyint(1)  default '1' not null,
    job             tinyint(1)  default '1' not null,
    education       tinyint(1)  default '1' not null,
    profilePhoto    tinyint(1)  default '1' not null,
    coverPhoto      tinyint(1)  default '1' not null,
    deletedStatus   varchar(50) default 'ACTIVE',
    dateCreated     timestamp   default current_timestamp(),
    dateDeleted     timestamp               null,
    comment         varchar(50)             null,
    constraint visibility_userId_uindex
        unique (userId),
    constraint visibility_users_fk
        foreign key (userId) references users (id)
);

insert into users (id, username,password,enabled,firstName, email,profilePhoto)
values (1,'user','password',1,'user','default@email.com','profile-default.jpg');

insert into authorities (id, username, authority, deletedStatus, userId)
values (1, 'user', 'ROLE_ANONYMOUS', 'ACTIVE', 1);

insert into visibility
values (1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 'ACTIVE', current_timestamp(), null, null);


