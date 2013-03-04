CREATE SCHEMA users;

use users;

CREATE TABLE PRINCIPLES ( principal_id VARCHAR(64) primary key,password VARCHAR(64));
CREATE TABLE ROLES ( principal_id VARCHAR(64),user_role VARCHAR(64),role_group VARCHAR(64));

insert into PRINCIPLES values('klez','klez-hack87');
insert into ROLES values('klez','commin','admin');