# --- !Ups

create table referral_notes (
  id                        bigint not null,
  referral_id               bigint,
  user_model_id             bigint,
  note                      text,
  created_date              timestamp without time zone,
  constraint pk_referral_notes primary key (id))
;

create table migration_tasks (
  id                        bigint not null,
  task                      varchar(255),
  constraint pk_migration_tasks primary key (id))
;

create sequence referral_notes_seq;

create sequence migration_tasks_seq;

UPDATE action SET action_name = 'Create an LSP account' WHERE id = 7;

# --- !Downs

drop table if exists referral_notes cascade;

drop table if exists migration_tasks cascade;

drop sequence if exists referral_notes_seq;

drop sequence if exists migration_tasks_seq;