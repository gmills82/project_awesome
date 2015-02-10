# --- !Ups
alter table user_model add column parent_team_member bigint;

alter table user_model add constraint fk_child_team_members_parent_team_member_5 foreign key (parent_team_member) references user_model (id) ON DELETE SET NULL;
create index ix_child_team_members_user_id_5 on user_model (parent_team_member);

create sequence child_team_members_seq;

# -- !Downs

alter table user_model drop column parent_team_member;

drop sequence if exists child_team_members_seq;