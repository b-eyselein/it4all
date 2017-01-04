package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import model.exercise.Exercise;
import model.programming.ProgrammingExercise;

@Entity
public class PythonExercise extends Exercise implements ProgrammingExercise {

  @Id
  public int id;

  @Column(columnDefinition = "text")
  public String text;
  
  @Override
  public String getDeclaration() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IntExerciseIdentifier getExerciseIdentifier() {
    return new IntExerciseIdentifier(id);
  }

  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getInputcount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getLanguage() {
    // TODO Auto-generated method stub
    return "python";
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public String getSampleSolution() {
    return "for i in range(7, 23):\n\tprint(i)";
  }

  @Override
  public String getTestdataValidationUrl() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTestingUrl() {
    return controllers.python.routes.Python.commit(getExerciseIdentifier()).url();
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

}
