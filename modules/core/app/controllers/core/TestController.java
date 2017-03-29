package controllers.core;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import model.Util;
import model.choicequestions.MCQuestion;
import play.data.FormFactory;
import play.mvc.Result;

public class TestController extends ExerciseController {

  // @formatter:off
  private static final List<MCQuestion> questions = Arrays.asList(
    new MCQuestion(1, "Frage ...", Arrays.asList(
        "Antwortmöglichkeit 1",
        "Antwortmöglichkeit 2",
        "Antwortmöglichkeit 3",
        "Antwortmöglichkeit 4")),
    new MCQuestion(2, "Frage ...", Arrays.asList(
        "Antwortmöglichkeit 1",
        "Antwortmöglichkeit 2",
        "Antwortmöglichkeit 3",
        "Antwortmöglichkeit 4",
        "Antwortmöglichkeit 5",
        "Antwortmöglichkeit 6"))
  );
  // @formatter:on

  @Inject
  public TestController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result testMC() {
    return ok(views.html.mctest.render(getUser(), questions));
  }

}
