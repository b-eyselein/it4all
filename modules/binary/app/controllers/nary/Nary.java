package controllers.nary;

import java.util.Random;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.NAryAddResult;
import model.NAryConvResult;
import model.NAryNumber;
import model.NumberBase;
import model.TwoCompResult;
import play.data.FormFactory;
import play.mvc.Result;

public class Nary extends ExerciseController {

  private static final Random GENERATOR = new Random();

  @Inject
  public Nary(FormFactory theFactory) {
    super(theFactory, "nary");
  }

  public Result checkNaryAdditionSolution() {
    NAryAddResult result = NAryAddResult.parseFromForm(factory.form().bindFromRequest());
    return ok(views.html.nAryAdditionResult.render(getUser(), result));
  }

  public Result checkNaryConversionSolution() {
    NAryConvResult result = NAryConvResult.parseFromForm(factory.form().bindFromRequest());
    return ok(views.html.nAryConversionResult.render(getUser(), result));
  }

  public Result checkTwoComplementSolution(boolean verbose) {
    TwoCompResult result = TwoCompResult.parseFromForm(factory.form().bindFromRequest());
    return ok(views.html.twoComplementResult.render(getUser(), result, verbose));
  }

  public Result index() {
    return ok(views.html.overview.render(getUser()));
  }

  public Result newNaryAdditionQuestion() {
    NumberBase base = NumberBase.values()[GENERATOR.nextInt(3)];

    int sum = GENERATOR.nextInt(255) + 1;
    int firstSummand = GENERATOR.nextInt(sum);

    NAryNumber first = new NAryNumber(firstSummand, base);
    NAryNumber second = new NAryNumber(sum - firstSummand, base);

    return ok(views.html.nAryAdditionQuestion.render(getUser(), first, second, base));
  }

  public Result newNaryConversionQuestion() {
    int value = GENERATOR.nextInt(256);

    int from = GENERATOR.nextInt(4);
    int to = GENERATOR.nextInt(4);
    while(to == from)
      to = GENERATOR.nextInt(4);

    NumberBase fromNB = NumberBase.values()[from];
    NumberBase toNB = NumberBase.values()[to];

    NAryNumber startingNumber = new NAryNumber(value, fromNB);

    return ok(views.html.nAryConversionQuestion.render(getUser(), value, fromNB, toNB, startingNumber));
  }

  public Result newTwoComplementQuestion(boolean verbose) {
    int value = -GENERATOR.nextInt(256);

    return ok(views.html.twoComplementQuestion.render(getUser(), value, verbose));
  }
}