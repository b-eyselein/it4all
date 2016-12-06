package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import model.exercise.Exercise;

@Entity
public class SpreadExercise extends Exercise {

  public static final Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);

  @Id
  public int id;

  public String title; // NOSONAR

  @Column(columnDefinition = "text")
  public String text;

  public String sampleFilename; // NOSONAR

  public String templateFilename; // NOSONAR

  public SpreadExercise(int theId) {
    id = theId;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + getId() + ": " + getTitle() + DIV_END);
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Aufgabentext: " + text + "</p>");
    builder.append("<p>Vorlagendateiname: &quot;" + templateFilename + "&quot;</p>");
    builder.append("<p>Musterdateiname: &quot;" + sampleFilename + "&quot;</p>");
    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }
}
