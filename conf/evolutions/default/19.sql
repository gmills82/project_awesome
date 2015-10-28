# --- !Ups

DELETE FROM "public"."action";

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  1,
  'Referral Phone Scripts',
  '/action/producerScript',
  200,
  'Phone scripts that lead to referalls.',
  'tool'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  2,
  'Track Agent efficiency',
  '/action/agents',
  10,
  'Track which agents send you the most profitable referrals',
  'tracking'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  3,
  'Create an agent account',
  '/signup/100',
  10,
  'Create an agent account for a team member',
  'admin'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  4,
  'Create an LSP account',
  '/signup/200',
  100,
  'Create a producer account for a team member',
  'admin'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  5,
  'Client Lookup',
  '/action/clients',
  200,
  'View Client Information',
  'tool'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  6,
  'Manage Client Referrals',
  '/action/referral',
  200,
  'Create a client referral',
  'tool'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  7,
  'View Team Referrals',
  '/action/team-referrals',
  100,
  'View your clients and their referrals',
  'tracking'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  8,
  'Daily Referrals Report',
  '/reports/daily-referrals',
  10,
  'Report of all team referrals created that day',
  'tracking'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  9,
  'Create assistant account',
  '/signup/1',
  10,
  'Create assistant account',
  'admin'
);

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  10,
  'Create sub producer',
  '/signup/10',
  10,
  'Create sub producer',
  'admin'
);

alter table "public"."user_model" drop CONSTRAINT IF EXISTS ck_user_model_role_type;

create table IF NOT EXISTS referral_notes (
  id                        bigint not null,
  referral_id               bigint,
  user_model_id             bigint,
  note                      text,
  created_date              timestamp without time zone,
  constraint pk_referral_notes primary key (id))
;

create table IF NOT EXISTS migration_tasks (
  id                        bigint not null,
  task                      varchar(255),
  constraint pk_migration_tasks primary key (id))
;

DROP SEQUENCE IF EXISTS referral_notes_seq;

create sequence referral_notes_seq;

DROP SEQUENCE IF EXISTS migration_tasks_seq;

create sequence migration_tasks_seq;

ALTER TABLE "public"."client" ADD COLUMN "group_id" BIGINT;

ALTER TABLE "public"."user_model" ADD COLUMN "group_id" BIGINT;

ALTER TABLE user_model DROP CONSTRAINT IF EXISTS ck_user_model_role_type;

UPDATE "user_model" SET role_type = 100 WHERE role_type = 1;

UPDATE "user_model" SET role_type = 200 WHERE role_type = 2;

# --- !Downs

ALTER TABLE "public"."user_model" ADD CONSTRAINT ck_user_model_role_type check (role_type in (0,1,2));

drop table if exists referral_notes cascade;

drop table if exists migration_tasks cascade;

drop sequence if exists referral_notes_seq;

drop sequence if exists migration_tasks_seq;

ALTER TABLE "public"."client" DROP COLUMN "group_id";

ALTER TABLE "public"."user_model" DROP COLUMN "group_id";

UPDATE "user_model" SET role_type = 0 WHERE role_type = 1;

UPDATE "user_model" SET role_type = 1 WHERE role_type = 100;

UPDATE "user_model" SET role_type = 2 WHERE role_type = 200;