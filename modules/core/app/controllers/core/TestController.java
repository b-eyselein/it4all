package controllers.core;

import javax.inject.Inject;

import model.Util;
import play.data.FormFactory;

public class TestController extends ExerciseController {
  
  @Inject
  public TestController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
}
