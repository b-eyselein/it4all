
# --- !Ups

insert into users (`name`, `std_role`, `todo`) values
	('bje40dc', 'SUPERADMIN', 'AGGREGATE'),
	('jok30ni', 'ADMIN', 'AGGREGATE'),
	('alg81dm', 'ADMIN', 'AGGREGATE'),
	('developer', 'SUPERADMIN', 'AGGREGATE')
	on duplicate key update std_role = values(std_role);

# --- !Downs