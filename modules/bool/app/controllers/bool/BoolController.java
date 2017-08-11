package controllers.bool;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.base.Splitter;

import controllers.core.BaseController;
import model.Assignment;
import model.BoolNodeParser;
import model.BooleanQuestion;
import model.BooleanQuestionResult;
import model.CorrectionException;
import model.CreationQuestion;
import model.FilloutQuestion;
import model.ScalaNode;
import model.StringConsts;
import model.exercise.Success;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class BoolController extends BaseController {
  
  private static final String ONE = "1";
  
  private static final Splitter COMMA_SPLITTER = Splitter.on(",");
  
  @Inject
  public BoolController(FormFactory theFactory) {
    super(theFactory);
  }
  
  public Result checkBoolCreationSolution() {
    DynamicForm form = factory.form().bindFromRequest();
    
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    String allVars = form.get("vars");
    
    Set<Character> variables = COMMA_SPLITTER.splitToList(allVars).stream().map(var -> Character.valueOf(var.charAt(0)))
        .collect(Collectors.toSet());
    
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    assignments.forEach(assignment -> assignment.setAssignment(BooleanQuestion.SOLUTION_VARIABLE,
        ONE.equals(form.get(assignment.toString())) ? true : false));
    
    CreationQuestion question = new CreationQuestion(variables, assignments);
    
    try {
      ScalaNode formula = BoolNodeParser.parse(learnerSolution);
      
      for(Assignment assignment: question.getSolutions())
        assignment.setAssignment(BooleanQuestion.LEARNER_VARIABLE, formula.evaluate(assignment));
      
      BooleanQuestionResult result = new BooleanQuestionResult(Success.PARTIALLY, learnerSolution, question);
      
      if(wantsJsonResponse())
        return ok(Json.toJson(result));
      else
        return ok(views.html.boolcreatesolution.render(getUser(), question));
      
    } catch (CorrectionException e) {
      // FIXME: Anzeige Parsing-fehler?
      return ok(Json.toJson(new BooleanQuestionResult(Success.NONE, learnerSolution, question,
          e.getMessage() /* + ": <code>" + e.getFormula() + "</code>" */)));
    }
  }
  
  public Result checkBoolFilloutSolution() {
    DynamicForm form = factory.form().bindFromRequest();
    
    String learnerFormula = form.get(StringConsts.FORM_VALUE);
    
    try {
      ScalaNode formula = BoolNodeParser.parse(learnerFormula);
      
      FilloutQuestion question = new FilloutQuestion(formula);
      
      for(Assignment assignment: question.getAssignments()) {
        assignment.setAssignment(BooleanQuestion.SOLUTION_VARIABLE, formula.evaluate(assignment));
        assignment.setAssignment(BooleanQuestion.LEARNER_VARIABLE, ONE.equals(form.get(assignment.toString())));
      }
      
      return ok(views.html.boolfilloutsolution.render(getUser(), question));
    } catch (CorrectionException e) {
      // FIXME: implement return!
      e.printStackTrace();
      return badRequest();
    }
  }
  
  public Result index() {
    return ok(views.html.booloverview.render(getUser()));
  }
  
  public Result newBoolCreationQuestion() {
    return ok(views.html.boolcreatequestion.render(getUser(), CreationQuestion.generateNew()));
  }
  
  public Result newBoolFilloutQuestion() {
    return ok(views.html.boolfilloutquestion.render(getUser(), FilloutQuestion.generateNew()));
  }
  
  private boolean wantsJsonResponse() {
    // FIXME: remove!
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }
}
