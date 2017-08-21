package controllers;

import java.util.Random;

import javax.inject.Inject;

import controllers.core.BaseController;
import model.nary.NAryAddResult;
import model.nary.NAryConvResult;
import model.nary.NAryNumber;
import model.nary.NumberBase;
import model.nary.NumberBase$;
import model.nary.TwoCompResult;
import model.nary.TwoCompResult$;
import play.data.FormFactory;
import play.mvc.Result;

public class NaryController extends BaseController {

  private static final Random GENERATOR = new Random();

  @Inject
  public NaryController(FormFactory theFactory) {
    super(theFactory);
  }

  public Result checkNaryAdditionSolution() {
    NAryAddResult result = NAryAddResult.parseFromForm(factory.form().bindFromRequest());
    return ok(views.html.nary.nAryAdditionResult.render(getUser(), result));
  }

  public Result checkNaryConversionSolution() {
    NAryConvResult result = NAryConvResult.parseFromForm(factory.form().bindFromRequest());
    return ok(views.html.nary.nAryConversionResult.render(getUser(), result));
  }

  public Result checkTwoComplementSolution(boolean verbose) {
    TwoCompResult result = TwoCompResult$.MODULE$.parseFromForm(factory.form().bindFromRequest(), verbose);
    return ok(views.html.nary.twoComplementResult.render(getUser(), result, verbose));
  }

  public Result index() {
    return ok(views.html.nary.overview.render(getUser()));
  }

  public Result newNaryAdditionQuestion() {
    NumberBase base = NumberBase$.MODULE$.takeRandom();

    int sum = GENERATOR.nextInt(255) + 1;
    int firstSummand = GENERATOR.nextInt(sum);

    NAryNumber first = new NAryNumber(firstSummand, base);
    NAryNumber second = new NAryNumber(sum - firstSummand, base);

    return ok(views.html.nary.nAryAdditionQuestion.render(getUser(), first, second, base));
  }

  public Result newNaryConversionQuestion() {
    int value = GENERATOR.nextInt(256);

    int from = GENERATOR.nextInt(4);
    int to = GENERATOR.nextInt(4);
    while(to == from)
      to = GENERATOR.nextInt(4);

    NumberBase fromNB = NumberBase$.MODULE$.takeRandom();
    NumberBase toNB = NumberBase$.MODULE$.takeRandom();

    NAryNumber startingNumber = new NAryNumber(value, fromNB);

    return ok(views.html.nary.nAryConversionQuestion.render(getUser(), value, fromNB, toNB, startingNumber));
  }

  public Result newTwoComplementQuestion(boolean verbose) {
    // Max negative number in two complement: -128
    int value = -GENERATOR.nextInt(129);
    return ok(views.html.nary.twoComplementQuestion.render(getUser(), value, verbose));
  }
}