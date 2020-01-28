# --- !Ups

alter table exercises
    add column difficulty int default null;

# --- !Downs

alter table exercises
    drop column difficulty;
