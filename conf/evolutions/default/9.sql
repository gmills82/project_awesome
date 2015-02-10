# --- !Ups
alter table referral add column last_edited_date varchar(255);
Update referral set last_edited_date = next_step_date;

# --- !Downs
alter table referral drop column last_edited_date;