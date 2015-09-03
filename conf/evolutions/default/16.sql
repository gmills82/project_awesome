# --- !Ups
DROP TABLE SCRIPT;

# --- !Downs
create table script (
  id                        bigint not null,
  type                      text,
  heading                   text,
  body                      text,
  constraint pk_script primary key (id));
