package controllers.core;

import java.util.Arrays;

import javax.inject.Inject;

import model.Util;
import play.data.FormFactory;
import play.mvc.Result;

public class TestController extends ExerciseController {

  @Inject
  public TestController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result testMC() {
    return ok(views.html.mctest.render(getUser(), Arrays.asList("Frage_1", "Frage_2")));
  }
  
}
