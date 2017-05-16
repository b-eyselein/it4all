package model.correctors;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  
  public List<ProgEvaluationResult> correct(ProgExercise exercise, String learnerSolution,
      List<CommitedTestData> userTestData) {
    ScriptEngine engine = MANAGER.getEngineByName(language.getEngineName());
    
    // TODO: Musteroutput mit gegebener Musterlösung berechnen statt angeben?
    
    // Evaluate learner solution
    try {
      engine.eval(learnerSolution);
    } catch (ScriptException e) { // NOSONAR
      // return Arrays.asList(new EvaluationFailed("Es gab einen Fehler beim
      // Einlesen ihrer Lösung:",
      // "<pre>" + e.getLocalizedMessage() + "</pre>"));
      return Collections.emptyList();
    }
    
    return Stream.concat(exercise.sampleTestData.stream(), userTestData.stream())
        .map(test -> evaluate(exercise, test, engine)).collect(Collectors.toList());
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
      return new ExecutionResult(learnerSolution, result != null ? result.toString() : "null",
          engine.getContext().getWriter().toString());
    } catch (ScriptException e) { // NOSONAR
      return new SyntaxError(learnerSolution, e.getMessage(), engine.getContext().getWriter().toString());
    }
  }
  
  private ProgEvaluationResult validataTestData(CommitedTestData data, ScriptEngine engine) {
    String toEvaluate = buildToEvaluate(data.exercise.functionName, data.getInput());
    return new ProgEvaluationResult(execute(toEvaluate, engine), data);
  }
  
  protected ProgEvaluationResult evaluate(ProgExercise exercise, ITestData testData, ScriptEngine engine) {
    String toEvaluate = buildToEvaluate(exercise.functionName, testData.getInput());
    AExecutionResult realResult = execute(toEvaluate, engine);
    
    return new ProgEvaluationResult(realResult, testData);
    // return validateResult(realResult.getResult(),
    // testData.getAwaitedResult());
  }
  
  protected abstract boolean validateResult(Object realResult, Object awaitedResult);
  
}