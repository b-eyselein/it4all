# --- !Ups

insert into users (username, pw_hash, std_role)
values ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin')
on duplicate key update std_role = values(std_role);

# --- !Downs

# noinspection SqlWithoutWhere
delete
from users;
