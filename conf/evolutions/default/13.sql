# --- !Ups
INSERT into action (id, action_name, action_url, required_permission_level, short_description, category) values (15, 'Daily Referrals Report', '/reports/daily-referrals', 0, 'Report of all team referrals created that day', 'tracking');

# --- !Downs
delete from action where id=15;