package model;

import javax.persistence.Column;
import javax.persistence.Entity;

import model.exercise.Exercise;

@Entity
public class UmlExercise extends Exercise {

  public static final com.avaje.ebean.Model.Finder<Integer, UmlExercise> finder = new com.avaje.ebean.Model.Finder<>(
      UmlExercise.class);

  @Column(columnDefinition = "text")
  public String classSelText;

  @Column(columnDefinition = "text")
  public String diagDrawText;

  @Column(columnDefinition = "text")
  public String diagDrawHelpText;

  public UmlExercise(int theId) {
    super(theId);
  }

  public String getExTextForClassSel() {
    return classSelText;
  }

  public String getExTextForDiagDraw() {
    return diagDrawText;
  }

  public String getExTextForDiagDrawHelp() {
    return diagDrawHelpText;
  }

  public UmlSolution getSolution() {
    // FIXME: member of class!
    return new UmlSolution();
  }

}