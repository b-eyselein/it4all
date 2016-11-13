package controllers.nary;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.BinaryBoolIdentifier;
import model.NAryAdditionQuestion;
import model.NAryConversionQuestion;
import model.NumberBase;
import model.Secured;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import views.html.naryadditionquestion;
import views.html.naryadditionsolution;
import views.html.naryconversionquestion;
import views.html.naryconversionsolution;
import views.html.overview;

@Security.Authenticated(Secured.class)
public class Nary extends ExerciseController<BinaryBoolIdentifier> {
  
  private static final String FORM_VALUE = "learnerSolution";
  
  @Inject
  public Nary(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result checkNaryAdditionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    String firstSumNAry = dynFormula.get("summand1");
    String secondSumNAry = dynFormula.get("summand2");
    int base = Integer.parseInt(dynFormula.get("base"));
    
    String committedLearnerSolution = dynFormula.get(FORM_VALUE);
    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");
    
    NAryAdditionQuestion question = new NAryAdditionQuestion(firstSumNAry, secondSumNAry, base, learnerSolInNAry);
    return ok(naryadditionsolution.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result checkNaryConversionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    String learnerSolution = dynFormula.get(FORM_VALUE).replaceAll("\\s", "");
    String value = dynFormula.get("value");
    int startingNB = Integer.parseInt(dynFormula.get("startingNB"));
    int targetNB = Integer.parseInt(dynFormula.get("targetNB"));
    
    NAryConversionQuestion question = new NAryConversionQuestion(value, NumberBase.getByBase(startingNB),
        NumberBase.getByBase(targetNB), learnerSolution);
    
    return ok(naryconversionsolution.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result index() {
    return ok(overview.render(UserManagement.getCurrentUser()));
  }

  public Result newNaryAdditionQuestion() {
    NAryAdditionQuestion question = NAryAdditionQuestion.generateNew();
    return ok(naryadditionquestion.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result newNaryConversionQuestion() {
    NAryConversionQuestion question = NAryConversionQuestion.generateNew();
    return ok(naryconversionquestion.render(UserManagement.getCurrentUser(), question));
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, BinaryBoolIdentifier exercise) {
    // FIXME: implement!
    return null;
  }
}