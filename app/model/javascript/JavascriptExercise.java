package model.javascript;

import java.util.Arrays;
import java.util.List;

public abstract class JavascriptExercise<ValueType, ResultType> {
  
  public final static class IntegerStandardTest extends JavascriptExercise<Integer, Long> {
    
    private static final List<JavascriptTest<Integer, Long>> STANDARD_EXERCISE_TESTS = Arrays.asList(
        new JavascriptTest<>(Arrays.asList(1, 1), 1L), new JavascriptTest<>(Arrays.asList(1, 2), 3L),
        new JavascriptTest<>(Arrays.asList(2, 2), 4L), new JavascriptTest<>(Arrays.asList(4, 7), 11L),
        new JavascriptTest<>(Arrays.asList(83, 74), 157L));
    
    private static final String EXERCISE_TEXT = "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.";
    private static final String SOLUTION_DEFAULT = "function sum(a, b) {\n  return 0;\n}";
    
    public IntegerStandardTest() {
      super(EXERCISE_TEXT, SOLUTION_DEFAULT, STANDARD_EXERCISE_TESTS);
    }
    
  }
  
  private String exerciseText;
  
  private String solutionDefault;
  
  private List<JavascriptTest<ValueType, ResultType>> tests;
  
  public JavascriptExercise(String exerciseText, String solutionDefault,
      List<JavascriptTest<ValueType, ResultType>> tests) {
    this.exerciseText = exerciseText;
    this.solutionDefault = solutionDefault;
    this.tests = tests;
  }
  
  public String getExerciseText() {
    return exerciseText;
  }
  
  public String getSolutionDefault() {
    return solutionDefault;
  }
  
  public List<JavascriptTest<ValueType, ResultType>> getTests() {
    return tests;
  }
  
}
