package model.programming;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import model.exercise.Success;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;

public abstract class ProgLangCorrector<E extends ProgrammingExercise> {

  protected static final ScriptEngineManager MANAGER = new ScriptEngineManager();
  protected String engineName;

  public ProgLangCorrector(String theEngineName) {
    engineName = theEngineName;
  }

  protected static <T> boolean validateResult(T gottenResult, T awaitedResult) {
    return gottenResult.equals(awaitedResult);
  }

  public CompleteResult correct(E exercise, String learnerSolution, List<ITestData> userTestData,
      User.SHOW_HIDE_AGGREGATE todo) {
    ScriptEngine engine = MANAGER.getEngineByName(engineName);

    // TODO: Musteroutput mit gegebener Musterlösung berechnen statt angeben?

    // Evaluate leaner solution
    try {
      engine.eval(learnerSolution);
    } catch (ScriptException e) { // NOSONAR
      return new CompleteResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Einlesen ihrer Lösung:",
              "<pre>" + e.getLocalizedMessage() + "</pre>")),
          todo);
    }

    List<EvaluationResult> results = Stream.concat(exercise.getFunctionTests().stream(), userTestData.stream())
        .map(test -> evaluate(exercise, test, engine)).collect(Collectors.toList());

    return new CompleteResult(learnerSolution, results, todo);
  }

  protected EvaluationResult evaluate(E exercise, ITestData testData, ScriptEngine engine) {
    String toEvaluate = testData.buildToEvaluate(exercise.functionname);
    IExecutionResult realResult = execute(toEvaluate, engine);

    return validateResult(exercise, testData, toEvaluate, realResult.getResult(), testData.getOutput(),
        "TODO: output...");
  }

  public IExecutionResult execute(String learnerSolution) {
    ScriptEngine engine = MANAGER.getEngineByName(engineName);

    ScriptContext context = new SimpleScriptContext();
    context.setWriter(new StringWriter());
    engine.setContext(context);

    return execute(learnerSolution, engine);
  }

  private IExecutionResult execute(String learnerSolution, ScriptEngine engine) {
    try {
      Object result = engine.eval(learnerSolution);
      // return new ExecutionResult(result,
      // engine.getContext().getWriter().toString(), "TODO: output...");
      return new ExecutionResult("TODO: awaitedResult?", Success.NONE, learnerSolution, result.toString(),
          engine.getContext().getWriter().toString());
    } catch (ScriptException e) {
      // Failure in script --> Return!
      Logger.debug("There has been an error in a script: " + e.getMessage(), e);
      return new SyntaxError(e.getCause().toString());
    }
  }

  protected abstract EvaluationResult validateResult(E exercise, ITestData testData, String toEvaluate,
      Object realResult, Object awaitedResult, String output);

}
