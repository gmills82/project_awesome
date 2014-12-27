# --- !Ups
ALTER table profile add column ref_id bigint;

# -- !Downs
alter table profile drop column ref_id;