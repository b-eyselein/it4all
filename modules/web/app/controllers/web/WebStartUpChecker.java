package controllers.web;

import java.util.List;

import controllers.core.StartUpChecker;
import model.html.WebExercise;

public class WebStartUpChecker extends StartUpChecker {
  
  @Override
  protected void performStartUpCheck() {
    theLogger.info("Running startup checks for Web");
    
    // FIXME: read Html + Css exercises from JSON-FILE?!?
    
    // Assert that there is at least one exercise for all types
    List<WebExercise> exercises = WebExercise.finder.all();
    if(exercises.isEmpty())
      theLogger.error("\t- No exercises found for Html!");
    else
      for(WebExercise exercise: exercises)
        if(exercise.htmlTasks.isEmpty())
          theLogger.error("\t- Html-Aufgabe " + exercise.id + " hat keine Tasks!");

  }
  
}
