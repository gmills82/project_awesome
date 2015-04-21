# --- !Ups
alter table client add column address1 varchar(255);
alter table client add column address2 varchar(255);
alter table client add column city varchar(255);
alter table client add column state varchar(255);
alter table client add column zipcode varchar(255);

# --- !Downs
alter table referral drop column address1;
alter table referral drop column address2;
alter table referral drop column city;
alter table referral drop column state;
alter table referral drop column zipcode;