# --- !Ups
alter table profile alter column liquidity_needs set data type text;
alter table profile alter column savings_plans set data type text;
alter table profile alter column expected_date_of_funds_use set data type text;
alter table profile alter column advisor_recommendation set data type text;
alter table profile alter column next_steps set data type text;

# -- !Downs
alter table profile alter column liquidity_needs set data type varchar(255);
alter table profile alter column savings_plans set data type varchar(255);
alter table profile alter column expected_date_of_funds_use set data type varchar(255);
alter table profile alter column advisor_recommendation set data type varchar(255);
alter table profile alter column next_steps set data type varchar(255);