package controllers.web;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import controllers.core.Util;
import model.css.CssExercise;
import model.html.HtmlExercise;
import model.javascript.JsExercise;
import play.Logger;

public class WebStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  
  private Util util;
  
  @Inject
  public WebStartUpChecker(Util theUtil) {
    util = theUtil;
    performStartUpCheck();
  }
  
  public void performStartUpCheck() {
    boolean noErrorsOrWarnigs = true;
    
    // TODO: Assert folder for solutions exists
    Path rootSolDir = util.getRootSolDir();
    if(!Files.exists(rootSolDir)) {
      theLogger.error("Folder for solutions does not exits!");
      noErrorsOrWarnigs = false;
    }
    
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

}
