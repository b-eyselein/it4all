# --- !Ups

INSERT INTO users (`username`, `pw_hash`, `std_role`, `todo`) VALUES
  ('bje40dc', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin', 'AGGR'),
  ('jok30ni', '', 'RoleAdmin', 'AGGR'),
  ('alg81dm', '', 'RoleAdmin', 'AGGR'),
  ('s319787', '', 'RoleAdmin', 'AGGR'),
  ('s323295', '', 'RoleAdmin', 'AGGR'),
  ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin', 'AGGR')
ON DUPLICATE KEY UPDATE std_role = VALUES(std_role);

# --- !Downs

DELETE FROM `users`;