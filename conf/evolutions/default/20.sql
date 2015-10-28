# --- !Ups

DELETE FROM "action" WHERE "action_name" = 'Track Agent efficiency';

# --- !Downs

INSERT INTO "action"(
  "id",
  "action_name",
  "action_url",
  "required_permission_level",
  "short_description",
  "category"
) VALUES (
  2,
  'Track Agent efficiency',
  '/action/agents',
  10,
  'Track which agents send you the most profitable referrals',
  'tracking'
);