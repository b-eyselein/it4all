package controllers.uml;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.Util;
import play.data.FormFactory;
import play.mvc.Result;

public class UML extends ExerciseController {
  
  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result index() {
    return ok("TODO!");
  }
  
}
