package controllers.binary;

import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.bool.BooleanQuestion;
import model.bool.BoolescheFunktionParser;
import model.bool.CreationQuestion;
import model.bool.FilloutQuestion;
import model.bool.tree.Assignment;
import model.bool.tree.BoolescheFunktionTree;
import model.nary.NAryAdditionQuestion;
import model.nary.NAryConversionQuestion;
import model.nary.NumberBase;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.boolcreatequestion;
import views.html.boolcreatesolution;
import views.html.boolfilloutquestion;
import views.html.boolfilloutsolution;
import views.html.naryadditionquestion;
import views.html.naryadditionsolution;
import views.html.naryconversionquestion;
import views.html.naryconversionsolution;
import views.html.overview;

@Security.Authenticated(Secured.class)
public class NaryBool extends Controller {
  
  @Inject
  private FormFactory factory;
  
  public Result checkBoolCreationSolution() {
    DynamicForm dynForm = factory.form().bindFromRequest();
    String learnerSolution = dynForm.get("learnerSolution");
    
    String joinedVars = dynForm.get("vars");
    Character[] variables = new Character[joinedVars.length()];
    for(int i = 0; i < joinedVars.length(); i++)
      variables[i] = joinedVars.charAt(i);
    
    BoolescheFunktionTree formula = BoolescheFunktionParser.parse(learnerSolution);
    
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    for(Assignment assignment: assignments) {
      boolean value = dynForm.get(assignment.toString()).equals("1") ? true : false;
      assignment.setAssignment(BooleanQuestion.SOLUTION_VARIABLE, value);
      assignment.setAssignment(BooleanQuestion.LEARNER_VARIABLE, formula.evaluate(assignment));
    }
    
    CreationQuestion question = new CreationQuestion(variables, assignments, formula.getAsString());
    
    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(question));
    else
      return ok(boolcreatesolution.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result checkBoolFilloutSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    char solVar = BooleanQuestion.SOLUTION_VARIABLE, learnerVal = BooleanQuestion.LEARNER_VARIABLE;
    
    String formula = dynFormula.get("formula");
    BoolescheFunktionTree bft = BoolescheFunktionParser.parse(formula);
    FilloutQuestion question = new FilloutQuestion(bft.getVariables(), bft);
    
    for(Assignment assignment: question.getAssignments()) {
      String wert = dynFormula.get(assignment.toString());
      assignment.setAssignment(solVar, bft.evaluate(assignment));
      assignment.setAssignment(learnerVal, wert.equals("1"));
    }
    
    return ok(boolfilloutsolution.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result checkNaryAdditionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    String firstSumNAry = dynFormula.get("summand1");
    String secondSumNAry = dynFormula.get("summand2");
    int base = Integer.parseInt(dynFormula.get("base"));
    
    String committedLearnerSolution = dynFormula.get("learnerSolution");
    // Replace all spaces, reverse to compensate input from right to left!
    String learnerSolInNAry = new StringBuilder(committedLearnerSolution).reverse().toString().replaceAll("\\s", "");
    
    NAryAdditionQuestion question = new NAryAdditionQuestion(firstSumNAry, secondSumNAry, base, learnerSolInNAry);
    return ok(naryadditionsolution.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result checkNaryConversionSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();
    
    String learnerSolution = dynFormula.get("learnerSolution").replaceAll("\\s", "");
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
  
  public Result newBoolCreationQuestion() {
    CreationQuestion question = CreationQuestion.generateNew();
    return ok(boolcreatequestion.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result newBoolFilloutQuestion() {
    FilloutQuestion question = FilloutQuestion.generateNew();
    return ok(boolfilloutquestion.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result newNaryAdditionQuestion() {
    NAryAdditionQuestion question = NAryAdditionQuestion.generateNew();
    return ok(naryadditionquestion.render(UserManagement.getCurrentUser(), question));
  }
  
  public Result newNaryConversionQuestion() {
    NAryConversionQuestion question = NAryConversionQuestion.generateNew();
    return ok(naryconversionquestion.render(UserManagement.getCurrentUser(), question));
  }
}