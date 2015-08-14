# --- !Ups
ALTER TABLE referral ADD COLUMN "next_step_timestamp" TIMESTAMP WITHOUT TIME ZONE;

# --- !Downs
ALTER TABLE referral DROP COLUMN "next_step_timestamp";