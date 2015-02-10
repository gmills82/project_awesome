# --- !Ups

alter table referral add column fresh boolean;

# --- !Downs

alter table referral drop column fresh;