package model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import model.programming.ProgrammingExercise;

@Entity
public class PythonExercise extends ProgrammingExercise<PythonTestData> {
  
  public static final Finder<Integer, PythonExercise> finder = new Finder<>(PythonExercise.class);
  
  public PythonExercise() {
    // Only for testing purposes! TODO: DELETE!
    id = 1;
    text = "Geben Sie eine die Summe beider Zahlen zurück!";
    declaration = "def sum(a, b):\n\treturn 0";
    sampleSolution = "def sum(a, b):\n\treturn a + b";
    functionname = "sum";
  }
  
  @Override
  public IntExerciseIdentifier getExerciseIdentifier() {
    return new IntExerciseIdentifier(id);
  }
  
  @Override
  public List<PythonTestData> getFunctionTests() {
    // TODO Auto-generated method stub
    return Arrays.asList(new PythonTestData(Arrays.asList("1", "1"), "2"),
        new PythonTestData(Arrays.asList("3", "4"), "7"));
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
