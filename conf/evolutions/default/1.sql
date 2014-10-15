# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table action (
  id                        bigint not null,
  action_name               varchar(255),
  action_url                varchar(255),
  required_permission_level integer,
  image_name                varchar(255),
  short_description         varchar(255),
  category                  varchar(255),
  constraint pk_action primary key (id))
;

create table client (
  id                        bigint not null,
  acct_number               varchar(255),
  name                      varchar(255),
  user_name                 varchar(255),
  phone_number              bigint,
  birth_date                bigint,
  birth_date_pretty         varchar(255),
  ref_notes                 varchar(255),
  constraint pk_client primary key (id))
;

create table debt (
  id                        bigint not null,
  client                    bigint not null,
  real_debt_type            integer,
  total_owed                float,
  frequency_str             varchar(255),
  frequency                 integer,
  recurring_amount          float,
  description               varchar(255),
  financial_institute       varchar(255),
  constraint ck_debt_real_debt_type check (real_debt_type in (0,1,2,3)),
  constraint pk_debt primary key (id))
;

create table financial_asset (
  id                        bigint not null,
  client                    bigint not null,
  total_value               float,
  real_asset_type           integer,
  frequency_str             varchar(255),
  frequency                 integer,
  recurring_amount          float,
  financial_institute       varchar(255),
  description               varchar(255),
  constraint ck_financial_asset_real_asset_type check (real_asset_type in (0,1,2,3,4,5,6,7,8)),
  constraint pk_financial_asset primary key (id))
;

create table income (
  id                        bigint not null,
  client                    bigint not null,
  asset_id                  bigint,
  value                     float,
  frequency                 integer,
  income_type               integer,
  description               varchar(255),
  constraint ck_income_frequency check (frequency in (0,1,2,3,4)),
  constraint ck_income_income_type check (income_type in (0,1,2,3,4)),
  constraint pk_income primary key (id))
;

create table profile (
  id                        bigint not null,
  agent_id                  bigint,
  client_id                 bigint,
  created_date              bigint,
  risk_tolerance            integer,
  risk_tolerance_modifier   integer,
  adjusted_risk_tolerance   integer,
  liquidity_needs           varchar(255),
  savings_plans             varchar(255),
  expected_date_of_funds_use varchar(255),
  advisor_recommendation    varchar(255),
  next_steps                varchar(255),
  notes                     varchar(255),
  constraint pk_profile primary key (id))
;

create table referral (
  id                        bigint not null,
  creator_id                bigint,
  agent_id                  bigint,
  client_id                 bigint,
  date_created              bigint,
  next_step_date            timestamp,
  reason_for_referral       varchar(255),
  creator_notes             varchar(255),
  was_productive            boolean,
  constraint pk_referral primary key (id))
;

create table script (
  id                        bigint not null,
  type                      varchar(255),
  heading                   varchar(255),
  body                      varchar(255),
  constraint pk_script primary key (id))
;

create table user (
  id                        bigint not null,
  user_id                   bigint,
  user_name                 varchar(255),
  password                  varchar(255),
  role_type                 integer,
  constraint ck_user_role_type check (role_type in (0,1,2)),
  constraint uq_user_user_name unique (user_name),
  constraint pk_user primary key (id))
;

create sequence action_seq;

create sequence client_seq;

create sequence debt_seq;

create sequence financial_asset_seq;

create sequence income_seq;

create sequence profile_seq;

create sequence referral_seq;

create sequence script_seq;

create sequence user_seq;

alter table debt add constraint fk_debt_client_1 foreign key (client) references client (id) on delete restrict on update restrict;
create index ix_debt_client_1 on debt (client);
alter table financial_asset add constraint fk_financial_asset_client_2 foreign key (client) references client (id) on delete restrict on update restrict;
create index ix_financial_asset_client_2 on financial_asset (client);
alter table income add constraint fk_income_client_3 foreign key (client) references client (id) on delete restrict on update restrict;
create index ix_income_client_3 on income (client);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists action;

drop table if exists client;

drop table if exists debt;

drop table if exists financial_asset;

drop table if exists income;

drop table if exists profile;

drop table if exists referral;

drop table if exists script;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists action_seq;

drop sequence if exists client_seq;

drop sequence if exists debt_seq;

drop sequence if exists financial_asset_seq;

drop sequence if exists income_seq;

drop sequence if exists profile_seq;

drop sequence if exists referral_seq;

drop sequence if exists script_seq;

drop sequence if exists user_seq;

