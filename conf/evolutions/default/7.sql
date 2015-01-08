# --- !Ups
alter table referral alter column reason_for_referral set data type text;
alter table referral alter column ref_notes set data type text;
alter table client alter column ref_notes set data type text;

# -- !Downs
alter table referral alter column reason_for_referral set data type varchar(255);
alter table referral alter column ref_notes set data type text varchar(255);
alter table client alter column ref_notes set data type text varchar(255);