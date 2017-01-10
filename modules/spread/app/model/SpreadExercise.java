package model;

import javax.persistence.Entity;

import model.exercise.Exercise;

@Entity
public class SpreadExercise extends Exercise {

  public static final Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);

  public String sampleFilename; // NOSONAR

  public String templateFilename; // NOSONAR

  public SpreadExercise(int theId) {
    super(theId);
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + id + ": " + title + DIV_END);
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Aufgabentext: " + text + "</p>");
    builder.append("<p>Vorlagendateiname: &quot;" + templateFilename + "&quot;</p>");
    builder.append("<p>Musterdateiname: &quot;" + sampleFilename + "&quot;</p>");
    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }
}
