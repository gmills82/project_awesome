# --- !Ups
alter table referral drop column fresh;

# -- !Downs
alter table referral add column fresh boolean;