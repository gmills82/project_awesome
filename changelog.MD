# Queries

## Run SQL to add the new column to the referrals table

BEGIN;


-- CREATE FIELD "next_step_timestamp" --------------------------
ALTER TABLE "public"."referral" ADD COLUMN "next_step_timestamp" TIMESTAMP WITHOUT TIME ZONE;
-- -------------------------------------------------------------;

COMMIT;

## Run the migration endpoint to normalize the referral data

curl -X PUT http://localhost:9000/json/referral/patch