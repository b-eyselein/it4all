package model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import model.programming.ProgrammingExercise;

@Entity
public class PythonExercise extends ProgrammingExercise<PythonTestData> {

  public static final Finder<Integer, PythonExercise> finder = new Finder<>(PythonExercise.class);
  
  public PythonExercise() {
    // TODO: DELETE!
    // Only for testing purposes!
    id = 1;
    text = "Geben Sie eine 8 auf der Konsole aus und eine 5 zurück!";
    declaration = "def test():\n\tpass";
    sampleSolution = "def test():\n\tprint(8)\n\treturn 5";
  }

  @Override
  public IntExerciseIdentifier getExerciseIdentifier() {
    return new IntExerciseIdentifier(id);
  }

  @Override
  public List<PythonTestData> getFunctionTests() {
    // TODO Auto-generated method stub
    return Arrays.asList(new PythonTestData(), new PythonTestData());
  }

  @Override
  public String getLanguage() {
    return "python";
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
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
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

}
