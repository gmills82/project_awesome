# Queries

BEGIN;


-- CREATE FIELD "next_step_timestamp" --------------------------
ALTER TABLE "public"."referral" ADD COLUMN "next_step_timestamp" TIMESTAMP WITHOUT TIME ZONE;
-- -------------------------------------------------------------;

COMMIT;
