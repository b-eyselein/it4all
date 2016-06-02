package controllers.web;

import model.css.CssExercise;
import model.html.HtmlExercise;
import model.javascript.JsExercise;
import play.Logger;

public class WebStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");

  public static void performStartUpCheck() {
    boolean noErrorsOrWarnigs = true;

    // Assert that there is at least one exercise for all types
    if(HtmlExercise.finder.all().size() == 0) {
      theLogger.error("\t- No exercises found for Html!");
      noErrorsOrWarnigs = false;
    }
    if(CssExercise.finder.all().size() == 0) {
      theLogger.error("\t- No exercises found for CSS!");
      noErrorsOrWarnigs = false;
    }
    if(JsExercise.finder.all().size() == 0) {
      theLogger.error("\t- No exercises found for Javascript!");
      noErrorsOrWarnigs = false;
    }

    if(noErrorsOrWarnigs)
      theLogger.info("\tStartUp-Check for Web successful.");
    
  }

  public WebStartUpChecker() {
    performStartUpCheck();
  }

}
