package controllers.web;

import java.util.List;

import model.html.HtmlExercise;
import model.javascript.JsExercise;
import play.Logger;

public class WebStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");

  public static void performStartUpCheck() {
    
    // Assert that there is at least one exercise for all types
    List<HtmlExercise> exercises = HtmlExercise.finder.all();
    if(exercises.size() == 0)
      theLogger.error("\t- No exercises found for Html!");
    else
      for(HtmlExercise exercise: exercises)
        if(exercise.tasks.size() == 0)
          theLogger.error("\t- Html-Aufgabe " + exercise.id + " hat keine Tasks!");
    
    if(JsExercise.finder.all().size() == 0)
      theLogger.error("\t- No exercises found for Javascript!");
    
  }

  public WebStartUpChecker() {
    performStartUpCheck();
  }

}
