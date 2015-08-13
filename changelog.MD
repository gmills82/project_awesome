# Queries
_Please backup the tables before running these statements_

## Add Timestamp Column to Referrals Table

This new column deprecates the `next_steps` column, which is a date stored as a string value.

    BEGIN;
    -- CREATE FIELD "next_step_timestamp" --------------------------
    ALTER TABLE "public"."referral" ADD COLUMN "next_step_timestamp" TIMESTAMP WITHOUT TIME ZONE;
    -- -------------------------------------------------------------;
    COMMIT;

## Delete Duplicate Clients With No Referrals

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

# cURL
Run the following cURL to populate the referral table with the newly added `next_step_timestamp` column.
    
    curl -X PUT http://localhost:9000/json/referral/patch
