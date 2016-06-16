create table phone (
	username		varchar(20) 	primary key,
	phonenumber		varchar(20) 	not null
);

insert into phone (`username`, `phonenumber`) values
	('First User', '0123456789'),
	('Second User', '9876543210');