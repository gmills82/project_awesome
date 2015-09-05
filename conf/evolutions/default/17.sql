# --- !Ups
INSERT into action (id, action_name, action_url, required_permission_level, short_description, category) values (16, 'Password Change', '/action/password/change', 2, 'Change your password', 'admin');

# --- !Downs
delete action where id=16;