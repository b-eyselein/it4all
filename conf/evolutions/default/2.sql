# --- !Ups

insert into student values
	("s319286") on duplicate key update name = "s319286";

insert into htmlexercise values
	(1, "Terminabsprache Werkstatt", "TODO") on duplicate key update id = 1;

insert into task values
	("name", 1, 1, "taskDesc", "attrs", "elemname", "tagname", "title");
	
# --- !Downs

