# --- !Ups

create table "COMPANY" ("ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL);
create table "COMPUTER" ("ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL,"INTRODUCED" BIGINT,"DISCONTINUED" BIGINT,"COMPANY_ID" BIGINT);
create table "REVIEW" ("ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY, "USERNAME" VARCHAR NOT NULL, "TIMESTAMP" BIGINT NOT NULL, "SCORE" INT NOT NULL, "COMMENT" VARCHAR NOT NULL, "COMPUTER_ID" BIGINT NOT NULL)

# --- !Downs

drop table "COMPUTER";
drop table "COMPANY";
drop table "REVIEW";
