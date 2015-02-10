# --- !Ups
alter table client alter column phone_number set data type varchar(255);

# --- !Downs
alter table client alter column phone_number set data type bigint;