package model;

import javax.persistence.Column;
import javax.persistence.Entity;

import model.exercise.Exercise;

@Entity
public class UmlExercise extends Exercise {

  public static final Finder<Integer, UmlExercise> finder = new Finder<>(UmlExercise.class);
  
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

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

}