package model;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.programming.ITestData;
import model.programming.ProgrammingExercise;

@Entity
public class PythonExercise extends ProgrammingExercise {
  
  public static final Finder<Integer, PythonExercise> finder = new Finder<>(PythonExercise.class);
  
  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<PythonTestData> functionTests;
  
  public PythonExercise(int theId) {
    super(theId);
    // Only for testing purposes! TODO: DELETE!
    text = "Geben Sie eine die Summe beider Zahlen zurück!";
    declaration = "def sum(a, b):\n\treturn 0";
    sampleSolution = "def sum(a, b):\n\treturn a + b";
    functionname = "sum";
  }
  
  @Override
  public List<ITestData> getFunctionTests() {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }
  
  @Override
  public String getLanguage() {
    return "python";
  }

  @Override
  public String getTestdataValidationUrl() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String getTestingUrl() {
    return controllers.python.routes.Python.correctLive(id).url();
  }
  
}
