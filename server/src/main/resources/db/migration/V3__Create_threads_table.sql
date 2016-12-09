create table THREADS (
    THREADID varchar(64) not null,
    USERID varchar(64) not null,
    PRIMARY KEY (THREADID, USERID)
);
