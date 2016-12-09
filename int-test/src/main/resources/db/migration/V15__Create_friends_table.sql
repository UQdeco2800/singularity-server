create table FRIENDS (
    USERID   varchar(64) not null references USERS(USERID),
    FRIENDID varchar(64) not null references USERS(USERID),
    primary key (USERID, FRIENDID),
    constraint IRREFLEXIVE check (USERID <> FRIENDID)
);