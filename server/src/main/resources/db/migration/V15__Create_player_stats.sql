CREATE TABLE PLAYERSTATS (
    SCOREID varchar(64) PRIMARY KEY,
    USERID varchar(64) NOT NULL,
    TIMESTAMP timestamp NOT NULL,
    SCORE int NOT NULL,
    EXPERIENCE int NOT NULL,
    LEVEL int NOT NULL,
    FOREIGN KEY (USERID) REFERENCES USERS(USERID)
);