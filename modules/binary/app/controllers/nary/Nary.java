package controllers.nary;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.NAryAdditionQuestion;
import model.NAryConversionQuestion;
import model.NumberBase;
import model.StringConsts;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class Nary extends ExerciseController {
  
  private static final String SUMMAND = "summand";
  private static final String BASE = "base";
  
  @Inject
  public Nary(FormFactory theFactory) {
    super(theFactory, "nary");
  }
  
  public Result checkNaryAdditionSolution() {
    DynamicForm form = factory.form().bindFromRequest();
    
    form.rawData().forEach((k, v) -> System.out.println(k + " --> " + v));
    
    String firstSumNAry = form.get(SUMMAND + "1");
    String secondSumNAry = form.get(SUMMAND + "2");
    int base = Integer.parseInt(form.get(BASE));
    
    String committedLearnerSolution = form.get(StringConsts.FORM_VALUE);
    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");
    
    NAryAdditionQuestion question = new NAryAdditionQuestion(firstSumNAry, secondSumNAry, base, learnerSolInNAry);
    return ok(views.html.naryadditionsolution.render(getUser(), question));
  }
  
  public Result checkNaryConversionSolution() {
    DynamicForm form = factory.form().bindFromRequest();
    
    form.rawData().forEach((k, v) -> System.out.println(k + " --> " + v));

    String learnerSolution = form.get(StringConsts.FORM_VALUE).replaceAll("\\s", "");
    String value = form.get("value");
    NumberBase startingNB = NumberBase.getByBase(Integer.parseInt(form.get("startingNB")));
    NumberBase targetNB = NumberBase.getByBase(Integer.parseInt(form.get("targetNB")));
    
    NAryConversionQuestion question = new NAryConversionQuestion(value, startingNB, targetNB, learnerSolution);
    
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