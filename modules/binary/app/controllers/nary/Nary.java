package controllers.nary;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.NAryAdditionQuestion;
import model.NAryConversionQuestion;
import model.NumberBase;
import model.Util;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import views.html.naryadditionquestion;
import views.html.naryadditionsolution;
import views.html.naryconversionquestion;
import views.html.naryconversionsolution;
import views.html.overview;

public class Nary extends ExerciseController {

  private static final String SUMMAND = "summand";
  private static final String BASE = "base";

  @Inject
  public Nary(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result checkNaryAdditionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    String firstSumNAry = dynFormula.get(SUMMAND + "1");
    String secondSumNAry = dynFormula.get(SUMMAND + "2");
    int base = Integer.parseInt(dynFormula.get(BASE));

    String committedLearnerSolution = dynFormula.get(LEARNER_SOLUTION_VALUE);
    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");

    NAryAdditionQuestion question = new NAryAdditionQuestion(firstSumNAry, secondSumNAry, base, learnerSolInNAry);
    return ok(naryadditionsolution.render(getUser(), question));
  }

  public Result checkNaryConversionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    String learnerSolution = dynFormula.get(LEARNER_SOLUTION_VALUE).replaceAll("\\s", "");
    String value = dynFormula.get("value");
    int startingNB = Integer.parseInt(dynFormula.get("startingNB"));
    int targetNB = Integer.parseInt(dynFormula.get("targetNB"));

    NAryConversionQuestion question = new NAryConversionQuestion(value, NumberBase.getByBase(startingNB),
        NumberBase.getByBase(targetNB), learnerSolution);

    return ok(naryconversionsolution.render(getUser(), question));
  }

  public Result index() {
    return ok(overview.render(getUser()));
  }

  public Result newNaryAdditionQuestion() {
    NAryAdditionQuestion question = NAryAdditionQuestion.generateNew();
    return ok(naryadditionquestion.render(getUser(), question));
  }

  public Result newNaryConversionQuestion() {
    NAryConversionQuestion question = NAryConversionQuestion.generateNew();
    return ok(naryconversionquestion.render(getUser(), question));
  }
}