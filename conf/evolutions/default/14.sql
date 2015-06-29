# --- !Ups
ALTER TABLE referral ADD COLUMN advisor_recommendation text;
Alter table client drop column acct_number;
Alter table client drop column ref_notes;
Drop table income;
Update referral as r
set advisor_recommendation = p.advisor_recommendation
from profile as p
where p.ref_id=r.id;
drop table profile;


# --- !Downs
Alter table referral drop column advisor_recommendation;
alter table client add column acct_number varchar(255);
alter table client add column ref_notes text;
create table income (
  id                        bigint not null,
  client                    bigint not null,
  asset_id                  bigint,
  value                     float,
  frequency                 integer,
  income_type               integer,
  description               text,
  constraint ck_income_frequency check (frequency in (0,1,2,3,4)),
  constraint ck_income_income_type check (income_type in (0,1,2,3,4)),
  constraint pk_income primary key (id))
;
alter table referral drop column advisor_recommendation;
create table profile (
  id                        bigint not null,
  agent_id                  bigint,
  client_id                 bigint,
  created_date              bigint,
  risk_tolerance            integer,
  risk_tolerance_modifier   integer,
  adjusted_risk_tolerance   integer,
  liquidity_needs           text,
  savings_plans             text,
  expected_date_of_funds_use text,
  advisor_recommendation    text,
  next_steps                text,
  constraint pk_profile primary key (id))
;