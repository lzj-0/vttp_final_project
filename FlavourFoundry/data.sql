drop database if exists ff;

create database ff;

use ff;

create table users(
    id int primary key auto_increment,
    email varchar(50) unique not null,
    name varchar(50) not null,
    password text not null,
    imageUrl text,
    level int default 1,
    exp int default 0,
    credits int default 0,
    recipesTried int default 0,
    gatekeepNo int default 0,
    walletAddress text,
    tier varchar(20) default 'Bronze',
    isPremium boolean default false,

    constraint chk_credits check(credits >= 0)
);

create table level_tiers(
    level int primary key,
    min_exp int not null,
    max_exp int not null,
    tier varchar(20) not null,
    earnCredit int not null,
    gatekeepNo int not null
);

create table rewards(
    tier varchar(20) not null,
    act varchar(100) not null,
    exp int not null,
    credit int not null,
    isGatekeep boolean not null,
    constraint pk_tier_act primary key(tier, act, isGatekeep)
);

INSERT INTO users (email, name, password, imageUrl, `level`, `exp`, credits, recipesTried, gatekeepNo, walletAddress, tier, isPremium) VALUES('bb@gmail.com', 'John', '$2a$10$a4.mL5MApKytzAypjllDIeGrJVa0fuRPi508V/kRcKp705R0I0TdO', 'https://ffo.sgp1.digitaloceanspaces.com/profile/1.jpg', 20, 4405, 525, 40, 8, '0x8DEd86682dBdbE04C3f4AeC0D302215d28deA6bD', 'Gold', 0);
INSERT INTO users (email, name, password, imageUrl, `level`, `exp`, credits, recipesTried, gatekeepNo, walletAddress, tier, isPremium) VALUES('jay260399@gmail.com', 'Lee Zi Jie', '$2a$10$CSmTlo3mWEyH.AleV8WFhOdbspuPijF5CuTKL.2R.Qz/Mq0xUr/wq', 'https://ffo.sgp1.digitaloceanspaces.com/profile/default-profile.jpg', 14, 2210, 76, 19, 0, NULL, 'Silver', 0);
INSERT INTO users (email, name, password, imageUrl, `level`, `exp`, credits, recipesTried, gatekeepNo, walletAddress, tier, isPremium) VALUES('dd@gmail.com', 'Jane', '$2a$10$WT6cIjFQMrt8K433.lRiVeFifjh5uTTaF8caV2hNgmarIsRJq1FX2', 'https://ffo.sgp1.digitaloceanspaces.com/profile/default-profile.jpg', 4, 340, 0, 1, 0, '0x970cccf1eDca2542918bD2d79A4ef4409b1bdC10', 'Bronze', 1);

insert into rewards values
    ('Bronze', 'upload', 100, 0, false),
    ('Bronze', 'try', 80, 0, false),
    ('Bronze', 'review', 15, 0, false),
    ('Bronze', 'views100', 20, 0, false),
    ('Bronze', 'leaderboard', 200, 0, false),
    ('Silver', 'upload', 100, 0, false),
    ('Silver', 'try', 80, 0, false),
    ('Silver', 'review', 15, 0, false),
    ('Silver', 'views100', 20, 0, false),
    ('Silver', 'leaderboard', 200, 0, false),
    ('Gold', 'upload', 100, 0, false),
    ('Gold', 'try', 80, 0, false),
    ('Gold', 'review', 15, 0, false),
    ('Gold', 'views100', 20, 0, false),
    ('Gold', 'leaderboard', 200, 0, false),
    ('Diamond', 'upload', 100, 0, false),
    ('Diamond', 'try', 80, 0, false),
    ('Diamond', 'review', 15, 0, false),
    ('Diamond', 'views100', 20, 0, false),
    ('Diamond', 'leaderboard', 200, 0, false),
    ('Silver', 'gettry', 60, 10, true),
    ('Silver', 'getreview', 0, 3, true),
    ('Silver', 'views100', 10, 5, true),
    ('Gold', 'gettry', 40, 20, true),
    ('Gold', 'getreview', 0, 3, true),
    ('Gold', 'views100', 10, 10, true),
    ('Diamond', 'gettry', 40, 30, true),
    ('Diamond', 'getreview', 0, 6, true),
    ('Diamond', 'views100', 10, 20, true);


insert into level_tiers values
    (1, 0, 99, 'Bronze', 0, 0),
    (2, 100, 209, 'Bronze', 0, 0),
    (3, 210, 329, 'Bronze', 0, 0),
    (4, 330, 459, 'Bronze', 0, 0),
    (5, 460, 599, 'Bronze', 0, 0),
    (6, 600, 749, 'Bronze', 0, 0),
    (7, 750, 909, 'Bronze', 0, 0),
    (8, 910, 1079, 'Bronze', 0, 0),
    (9, 1080, 1259, 'Bronze', 0, 0),
    (10, 1260, 1459, 'Silver', 10, 3),
    (11, 1460, 1679, 'Silver', 10, 0),
    (12, 1680, 1919, 'Silver', 15, 0),
    (13, 1920, 2179, 'Silver', 15, 0),
    (14, 2180, 2459, 'Silver', 20, 0),
    (15, 2460, 2769, 'Silver', 20, 2),
    (16, 2770, 3109, 'Silver', 25, 0),
    (17, 3110, 3479, 'Silver', 25, 0),
    (18, 3480, 3889, 'Silver', 30, 0),
    (19, 3890, 4349, 'Silver', 30, 0),
    (20, 4350, 4859, 'Gold', 50, 2),
    (21, 4860, 5419, 'Gold', 50, 0),
    (22, 5420, 6039, 'Gold', 60, 0),
    (23, 6040, 6739, 'Gold', 60, 0),
    (24, 6740, 7549, 'Gold', 70, 0),
    (25, 7550, 8499, 'Gold', 70, 2),
    (26, 8500, 9619, 'Gold', 80, 0),
    (27, 9620, 10909, 'Gold', 80, 0),
    (28, 10910, 12379, 'Gold', 90, 0),
    (29, 12380, 13989, 'Gold', 90, 0),
    (30, 13990, 15779, 'Diamond', 100, 1),
    (31, 15780, 17749, 'Diamond', 200, 0),
    (32, 17750, 19909, 'Diamond', 300, 0),
    (33, 19910, 22269, 'Diamond', 400, 0),
    (34, 22270, 24839, 'Diamond', 500, 0),
    (35, 24840, 27629, 'Diamond', 600, 5),
    (36, 27630, 30639, 'Diamond', 700, 0),
    (37, 30640, 33879, 'Diamond', 800, 0),
    (38, 33880, 37359, 'Diamond', 900, 0),
    (39, 37360, 41089, 'Diamond', 1000, 0),
    (40, 41090, 100000, 'Diamond', 1000, 0);