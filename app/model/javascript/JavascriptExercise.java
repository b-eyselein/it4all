package model.javascript;

import java.util.Arrays;
import java.util.List;

public abstract class JavascriptExercise {
  
  public final static class IntegerStandardTest extends JavascriptExercise {
    
    private static final String EXERCISE_TEXT = "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt "
        + "und deren Summe zurückgibt.";
    
    private static final String SOLUTION_DEFAULT = "function sum(a, b) {\n  return 0;\n}";
    
    private static final List<JavascriptTest> STANDARD_EXERCISE_TESTS = Arrays.asList(
        new JavascriptTest(Arrays.asList("1", "1"), "2"), new JavascriptTest(Arrays.asList("1", "2"), "3"),
        new JavascriptTest(Arrays.asList("2", "2"), "4"), new JavascriptTest(Arrays.asList("4", "7"), "11"),
        new JavascriptTest(Arrays.asList("83", "74"), "157"));
    
    public IntegerStandardTest() {
      super(EXERCISE_TEXT, SOLUTION_DEFAULT, STANDARD_EXERCISE_TESTS, 1, "sum");
    }
    
  }
  
  public static final class StringStandardTest extends JavascriptExercise {
    
    private static final String EXERCISE_TEXT = "Implementieren Sie die folgende Funktion 'concat',"
        + " die drei beliebige Strings entgegennimmt und die Konkatenation der Strings zurückgibt.";
    
    private static final String SOLUTION_DEFAULT = "function concat(str1, str2, str3) {\n  return \"\";\n}";
    
    private static final List<JavascriptTest> STRING_TESTS = Arrays.asList(
        new JavascriptTest(Arrays.asList("\"Hallo \"", "\"schöne \"", "\"Welt.\""), "Hallo schöne Welt."),
        new JavascriptTest(Arrays.asList("\"Dies \"", "\"ist ein \"", "\"Test.\""), "Dies ist ein Test."));
    
    public StringStandardTest() {
      super(EXERCISE_TEXT, SOLUTION_DEFAULT, STRING_TESTS, 2, "concat");
    }
    
  }
  
  private int exerciseId;
  private String exerciseText;
  private String solutionDefault;
  private String functionName;
  private List<JavascriptTest> functionTests;
  
  public JavascriptExercise(String text, String defaultSol, List<JavascriptTest> tests, int id, String function) {
    exerciseId = id;
    exerciseText = text;
    solutionDefault = defaultSol;
    functionName = function;
    functionTests = tests;
  }
  
  public int getExerciseId() {
    return exerciseId;
  }
  
  public String getExerciseText() {
    return exerciseText;
  }
  
  public String getFunctionName() {
    return functionName;
  }
  
  public String getSolutionDefault() {
    return solutionDefault;
  }
  
  public List<JavascriptTest> getTests() {
    return functionTests;
  }
  
}
