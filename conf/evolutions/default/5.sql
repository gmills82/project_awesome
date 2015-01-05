# --- !Ups
ALTER table referral add column status varchar(255);
ALTER table referral add column t_insurance integer;
ALTER table referral add column t_pc integer;
ALTER table referral add column t_ips integer;

# -- !Downs
alter table referral drop column status;
alter table referral drop column t_insurance;
alter table referral drop column t_pc;
alter table referral drop column t_ips;