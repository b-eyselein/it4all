package controllers.nary;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.NAryAdditionQuestion;
import model.NAryConversionQuestion;
import model.NumberBase;
import model.StringConsts;
import model.Util;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

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

    String committedLearnerSolution = dynFormula.get(StringConsts.FORM_VALUE);
    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");

    NAryAdditionQuestion question = new NAryAdditionQuestion(firstSumNAry, secondSumNAry, base, learnerSolInNAry);
    return ok(views.html.naryadditionsolution.render(getUser(), question));
  }

  public Result checkNaryConversionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    String learnerSolution = dynFormula.get(StringConsts.FORM_VALUE).replaceAll("\\s", "");
    String value = dynFormula.get("value");
    int startingNB = Integer.parseInt(dynFormula.get("startingNB"));
    int targetNB = Integer.parseInt(dynFormula.get("targetNB"));

    NAryConversionQuestion question = new NAryConversionQuestion(value, NumberBase.getByBase(startingNB),
        NumberBase.getByBase(targetNB), learnerSolution);

    return ok(views.html.naryconversionsolution.render(getUser(), question));
  }

  public Result index() {
    return ok(views.html.overview.render(getUser()));
  }

  public Result newNaryAdditionQuestion() {
    NAryAdditionQuestion question = NAryAdditionQuestion.generateNew();
    return ok(views.html.naryadditionquestion.render(getUser(), question));
  }

  public Result newNaryConversionQuestion() {
    NAryConversionQuestion question = NAryConversionQuestion.generateNew();
    return ok(views.html.naryconversionquestion.render(getUser(), question));
  }
}