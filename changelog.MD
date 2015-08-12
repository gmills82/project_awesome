# Queries

## Run SQL to add the new column to the referrals table

BEGIN;


-- CREATE FIELD "next_step_timestamp" --------------------------
ALTER TABLE "public"."referral" ADD COLUMN "next_step_timestamp" TIMESTAMP WITHOUT TIME ZONE;
-- -------------------------------------------------------------;

COMMIT;

## Run SQL to delete duplicate clients with no linked referral record

DELETE FROM client
WHERE  id IN (SELECT client.id
              FROM   client
                     LEFT JOIN referral
                            ON referral.client_id = client.id
              WHERE  referral.client_id IS NULL
                     AND client.NAME IN (SELECT client.NAME
                                         FROM   client
                                         GROUP  BY client.NAME
                                         HAVING Count(*) > 1)) 

## Run the migration endpoint to normalize the referral data

curl -X PUT http://localhost:9000/json/referral/patch
