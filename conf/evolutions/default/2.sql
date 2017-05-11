
# --- !Ups

insert into users (`name`, `role`, `todo`) values
	('bje40dc', 'ADMIN', 'AGGREGATE'),
	('developer', 'ADMIN', 'AGGREGATE')
	on duplicate key update role = values(role);

# --- !Downs