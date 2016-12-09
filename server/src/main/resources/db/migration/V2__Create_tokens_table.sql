create table TOKENS (
    TOKENID varchar(64) PRIMARY KEY,
    EXPIRES bigint not null,
    USERID varchar(64) not null
);
