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

ALTER TABLE "public"."client" ADD COLUMN "group" BIGINT;

ALTER TABLE "public"."user_model" ADD COLUMN "group_id" BIGINT;

UPDATE "action" SET required_permission_level = 100 WHERE required_permission_level = 1;

UPDATE "action" SET required_permission_level = 200 WHERE required_permission_level = 2;

UPDATE "action" SET required_permission_level = 1 WHERE required_permission_level = 0;

ALTER TABLE user_model DROP CONSTRAINT IF EXISTS ck_user_model_role_type;

UPDATE "user_model" SET role_type = 100 WHERE role_type = 1;

UPDATE "user_model" SET role_type = 200 WHERE role_type = 2;

UPDATE "action" SET action_url = '/signup/100' WHERE action_url = '/signup/1';

UPDATE "action" SET action_url = '/signup/200' WHERE action_url = '/signup/2';

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  16,
  'Create assistant account',
  '/signup/1',
  1,
  'Create assistant account',
  'admin'
);


# --- !Downs

drop table if exists referral_notes cascade;

drop table if exists migration_tasks cascade;

drop sequence if exists referral_notes_seq;

drop sequence if exists migration_tasks_seq;

ALTER TABLE "public"."client" DROP COLUMN "group";

ALTER TABLE "public"."user_model" DROP COLUMN "group_id";

UPDATE "action" SET required_permission_level = 1 WHERE required_permission_level = 100;

UPDATE "action" SET required_permission_level = 2 WHERE required_permission_level = 200;

UPDATE "user_model" SET role_type = 0 WHERE role_type = 1;

UPDATE "user_model" SET role_type = 1 WHERE role_type = 100;

UPDATE "user_model" SET role_type = 2 WHERE role_type = 200;

UPDATE "action" SET action_url = '/signup/1' WHERE action_url = '/signup/100';

UPDATE "action" SET action_url = '/signup/2' WHERE action_url = '/signup/200';

delete from "action" where id = 16;