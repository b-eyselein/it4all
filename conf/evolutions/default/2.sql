
# --- !Ups

insert into users (`name`, `std_role`, `todo`) values
	('bje40dc', 'SUPERADMIN', 'AGGREGATE'),
	('jok30ni', 'ADMIN', 'AGGREGATE'),
	('alg81dm', 'ADMIN', 'AGGREGATE'),
	('s319787', 'ADMIN', 'AGGREGATE'),
	('s323295', 'ADMIN', 'AGGREGATE'),
	('developer', 'SUPERADMIN', 'AGGREGATE')
	ON DUPLICATE KEY UPDATE std_role = VALUES(std_role);

# --- !Downs