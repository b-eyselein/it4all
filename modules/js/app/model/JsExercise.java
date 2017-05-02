package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.programming.ITestData;
import model.programming.ProgrammingExercise;

@Entity
public class JsExercise extends ProgrammingExercise {

  public enum JsDataType {
    BOOLEAN, NUMBER, STRING, SYMBOL, UNDEFINED, NULL, OBJECT;
  }

  public static final Finder<Integer, JsExercise> finder = new Finder<>(JsExercise.class);

  public String inputtypes; // NOSONAR

  @Enumerated(EnumType.STRING)
  public JsDataType returntype;

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsTestData> functionTests;

  public JsExercise(int theId) {
    super(theId);
  }

  @Override
  public List<ITestData> getFunctionTests() {
    return new ArrayList<>(functionTests);
  }

  public List<JsDataType> getInputTypes() {
    return Arrays.stream(inputtypes.split("#")).map(JsDataType::valueOf).collect(Collectors.toList());
  }

  @Override
  public String getLanguage() {
    return "javascript";
  }

  @Override
  public String getTestdataValidationUrl() {
    return controllers.js.routes.JS.validateTestData(id).url();
  }

  @Override
  public String getTestingUrl() {
    return controllers.js.routes.JS.correctLive(id).url();
  }

}
