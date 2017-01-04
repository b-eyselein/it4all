package model.programming;

import java.io.StringWriter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import play.Logger;

public abstract class ProgLangCorrector {
  
  protected static final ScriptEngineManager MANAGER = new ScriptEngineManager();
  protected String engineName;
  
  public ProgLangCorrector(String theEngineName) {
    engineName = theEngineName;
  }
  
  public IExecutionResult execute(String learnerSolution) {
    ScriptEngine engine = MANAGER.getEngineByName(engineName);
    ScriptContext context = new SimpleScriptContext();
    context.setWriter(new StringWriter());
    engine.setContext(context);
    
    return execute(learnerSolution, engine);
  }
  
  public IExecutionResult execute(String learnerSolution, ScriptEngine engine) {
    try {
      Object result = engine.eval(learnerSolution);
      return new ExecutionResult(result, engine.getContext().getWriter().toString());
    } catch (ScriptException e) {
      // Failure in script --> Return!
      Logger.debug("There has been an error in a script: " + e.getMessage(), e);
      return new SyntaxError(e.getCause().toString());
    }
  }
  
}
