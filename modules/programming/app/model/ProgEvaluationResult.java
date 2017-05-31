package model;

import java.util.List;

import model.execution.AExecutionResult;
import model.execution.SyntaxError;
import model.exercise.Success;
import model.result.EvaluationResult;
import model.testdata.ITestData;

public class ProgEvaluationResult extends EvaluationResult {

  private AExecutionResult executionResult;
  private ITestData testData;

  public ProgEvaluationResult(AExecutionResult theExecutionResult, ITestData theTestData) {
    super(analyze(theExecutionResult, theTestData));
    executionResult = theExecutionResult;
    testData = theTestData;
  }

  private static Success analyze(AExecutionResult executionResult, ITestData testData) {
    if(executionResult instanceof SyntaxError)
      return Success.NONE;

    return validateResult(executionResult.getResult(), testData.output) ? Success.COMPLETE : Success.NONE;
  }

  protected static <T> boolean validateResult(T gottenResult, T awaitedResult) {
    // FIXME: validation of result is dependent on language! Example: numbers in
    return gottenResult.equals(awaitedResult);
  }

  public String getAwaitedResult() {
    return testData.output;
  }

  public AExecutionResult getExecutionResult() {
    return executionResult;
  }

  public int getId() {
    return testData.getId();
  }

  public List<String> getInput() {
    return testData.getInput();
  }

  public String getRealResult() {
    return executionResult.getResult();
  }

  // public String getAsHtml() {
  // FIXME: Auto-generated method stub
  // boolean successful = success == Success.COMPLETE;
  //
  // StringBuilder builder = new StringBuilder();
  // builder.append("<div class=\"col-md-6\">\n");
  // builder.append(" <div class=\"alert alert-" + (successful ? "success" :
  // "danger") + "\">\n");
  // builder.append(" <p>Test von <code>" + executionResult.getEvaluated() +
  // "</code> war "
  // + (successful ? "" : "nicht ") + "erfolgreich.<p>\n");
  // builder.append(" <p>Erwartet: \"" + testData.output + "\"</p>\n");
  // builder.append(" <p>Ergebnis: \"" + executionResult.getResult() +
  // "\"</p>\n");
  // builder.append(" " + DIV_END + "\n" + DIV_END + "");
  // return builder.toString();
  // }

  public String getTitleForValidation() {
    if(executionResult instanceof SyntaxError)
      return ((SyntaxError) executionResult).getErrorMessage();

    if(isSuccessful())
      return "Dieser Test war erfolgreich.";

    return "Dieser Test war nicht erfolgreich.\nErwartetes Ergebnisses: " + testData.output + "\nReales Ergebnis: "
        + executionResult.getResult();
  }

  @Override
  public String toString() {
    return testData.toString() + ", " + executionResult.getResult();
  }

}
