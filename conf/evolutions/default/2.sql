# --- !Ups

insert into users (user_type, username, std_role)
values (0, 'developer', 'RoleSuperAdmin')
on duplicate key update std_role = values(std_role);

insert into pw_hashes (username, pw_hash)
values ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG')
on duplicate key update pw_hash = values(pw_hash);

# --- !Downs
