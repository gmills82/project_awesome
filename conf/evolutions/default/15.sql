# --- !Downs
ALTER TABLE referral ADD COLUMN "next_step_timestamp" TIMESTAMP WITHOUT TIME ZONE;

# --- !Downs
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