package model.correctors;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import model.ProgEvaluationResult;
import model.ProgExercise;
import model.ProgLanguage;
import model.execution.AExecutionResult;
import model.execution.ExecutionResult;
import model.execution.SyntaxError;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import model.testdata.CommitedTestData;
import model.testdata.ITestData;
import play.Logger;

public abstract class ProgLangCorrector {

  protected static final ScriptEngineManager MANAGER = new ScriptEngineManager();
  private ProgLanguage language;

  public ProgLangCorrector(ProgLanguage theLanguage) {
    language = theLanguage;
  }

  public abstract String buildToEvaluate(String functionname, List<String> inputs);

  public CompleteResult correct(ProgExercise exercise, String learnerSolution, List<ITestData> userTestData) {
    ScriptEngine engine = MANAGER.getEngineByName(language.getEngineName());

    // TODO: Musteroutput mit gegebener Musterlösung berechnen statt angeben?

    // Evaluate learner solution
    try {
      engine.eval(learnerSolution);
    } catch (ScriptException e) { // NOSONAR
      return new CompleteResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim Einlesen ihrer Lösung:",
              "<pre>" + e.getLocalizedMessage() + "</pre>")));
    }

    List<ITestData> testData = new ArrayList<>(exercise.sampleTestData.size() + userTestData.size());
    testData.addAll(exercise.sampleTestData);
    testData.addAll(userTestData);

    List<EvaluationResult> results = testData.stream().map(t -> evaluate(exercise, t, engine))
        .collect(Collectors.toList());

    /*
     * FIXME: Stream.concat(...) wäre schöner, verursacht aber:
     * "ClassCastException: javassist.bytecode.InterfaceMethodrefInfo cannot be cast to javassist.bytecode.MethodrefInfo"
     */
    // Stream.concat(exercise.testdata.stream(), userTestData.stream()).map(test
    // -> evaluate(exercise, test, engine))
    // .collect(Collectors.toList());

    return new CompleteResult(learnerSolution, results);
  }

  // public IExecutionResult execute(String learnerSolution) {
  //// ScriptEngine engine = MANAGER.getEngineByName(engineName);
  ////
  //// ScriptContext context = new SimpleScriptContext();
  //// context.setWriter(new StringWriter());
  //// engine.setContext(context);
  ////
  //// return execute(learnerSolution, engine);
  // }

  public List<ProgEvaluationResult> validateTestData(ProgExercise exercise, List<CommitedTestData> testData) {
    try {
      ScriptEngine engine = MANAGER.getEngineByName(language.getEngineName());
      ScriptContext scriptContext = new SimpleScriptContext();
      scriptContext.setWriter(new StringWriter());
      engine.setContext(scriptContext);

      // Learn sample solution
      engine.eval(exercise.getSampleSolution(language));

      // Validate test data
      // FIXME: implement!
      return testData.stream().map(data -> validataTestData(data, engine)).collect(Collectors.toList());
    } catch (ScriptException e) {
      Logger.debug("There has been an error validating test data:", e);
      return Collections.emptyList();
    }
  }

  private AExecutionResult execute(String learnerSolution, ScriptEngine engine) {
    try {
      Object result = engine.eval(learnerSolution);
      // FIXME: result == null!
      return new ExecutionResult(learnerSolution, result != null ? result.toString() : "null",
          engine.getContext().getWriter().toString());
    } catch (ScriptException e) {
      // Failure in script --> Return!
      Logger.debug("There has been an error in a script: " + e.getMessage(), e);
      return new SyntaxError(learnerSolution, engine.getContext().getWriter().toString());
    }
  }

  private ProgEvaluationResult validataTestData(CommitedTestData data, ScriptEngine engine) {
    String funtionName = data.exercise.functionName;
    String toEvaluate = buildToEvaluate(funtionName, data.getInput());
    return new ProgEvaluationResult(execute(toEvaluate, engine), data);
  }

  protected EvaluationResult evaluate(ProgExercise exercise, ITestData testData, ScriptEngine engine) {
    String toEvaluate = buildToEvaluate(exercise.functionName, testData.getInput());
    AExecutionResult realResult = execute(toEvaluate, engine);

    return new ProgEvaluationResult(realResult, testData);
    // return validateResult(realResult.getResult(),
    // testData.getAwaitedResult());
  }

  protected abstract boolean validateResult(Object realResult, Object awaitedResult);

}