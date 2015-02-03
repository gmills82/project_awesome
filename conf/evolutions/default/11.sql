# --- !Ups
alter table action drop column image_name;
INSERT into action (id, action_name, action_url, required_permission_level, short_description, category) values (9, 'Manage Clients', '/action/clients', 0, 'Manage your clients', 'tool');

# --- !Downs
alter table action add column image_name set data type varchar(255);
delete action where id=9;