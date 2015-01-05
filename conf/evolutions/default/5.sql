# --- !Ups
ALTER table referral add column status varchar(255);
ALTER table referral add column t_insurance double;
ALTER table referral add column t_pc double;
ALTER table referral add column t_ips double;

# -- !Downs
alter table referral drop column status;
alter table referral drop column t_insurance;
alter table referral drop column t_pc;
alter table referral drop column t_ips;