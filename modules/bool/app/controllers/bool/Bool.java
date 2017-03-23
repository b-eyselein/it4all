package controllers.bool;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.BooleanParsingException;
import model.BooleanQuestion;
import model.BooleanQuestionResult;
import model.BoolescheFunktionParser;
import model.CreationQuestion;
import model.FilloutQuestion;
import model.Util;
import model.exercise.Success;
import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class Bool extends ExerciseController {
  private static final String FORM_VALUE = "learnerSolution";

  @Inject
  public Bool(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result checkBoolCreationSolution() {
    DynamicForm dynForm = factory.form().bindFromRequest();
    String learnerSolution = dynForm.get(FORM_VALUE);

    List<Character> variables = Arrays.stream(dynForm.get("vars").split(", "))
        .map(var -> Character.valueOf(var.charAt(0))).collect(Collectors.toList());

    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    for(Assignment assignment: assignments) {
      boolean value = "1".equals(dynForm.get(assignment.toString())) ? true : false;
      assignment.setAssignment(BooleanQuestion.SOLUTION_VARIABLE, value);
    }

    CreationQuestion question = new CreationQuestion(variables, assignments);

    try {
      BoolescheFunktionTree formula = BoolescheFunktionParser.parse(learnerSolution);

      for(Assignment assignment: question.getSolutions())
        assignment.setAssignment(BooleanQuestion.LEARNER_VARIABLE, formula.evaluate(assignment));

      BooleanQuestionResult result = new BooleanQuestionResult(Success.PARTIALLY, learnerSolution, question);

      if(wantsJsonResponse())
        return ok(Json.toJson(result));
      else
        return ok(views.html.boolcreatesolution.render(UserManagement.getCurrentUser(), question));

    } catch (BooleanParsingException e) {
      // FIXME: Anzeige Parsing-fehler?
      return ok(Json.toJson(new BooleanQuestionResult(Success.NONE, learnerSolution, question,
          e.getMessage() + ": <code>" + e.getFormula() + "</code>")));
    }
  }

  public Result checkBoolFilloutSolution() {
    DynamicForm dynFormula = factory.form().bindFromRequest();

    char solVar = BooleanQuestion.SOLUTION_VARIABLE;
    char learnerVal = BooleanQuestion.LEARNER_VARIABLE;

    String formula = dynFormula.get("formula");
    BoolescheFunktionTree bft;
    try {
      bft = BoolescheFunktionParser.parse(formula);
    } catch (BooleanParsingException e) {
      // FIXME: implement return!
      e.printStackTrace();
      return badRequest();
    }
    FilloutQuestion question = new FilloutQuestion(bft.getVariables(), bft);

    for(Assignment assignment: question.getAssignments()) {
      String wert = dynFormula.get(assignment.toString());
      assignment.setAssignment(solVar, bft.evaluate(assignment));
      assignment.setAssignment(learnerVal, "1".equals(wert));
    }

    return ok(views.html.boolfilloutsolution.render(UserManagement.getCurrentUser(), question));
  }

  public Result index() {
    return ok(views.html.booloverview.render(UserManagement.getCurrentUser()));
  }

  public Result newBoolCreationQuestion() {
    CreationQuestion question = CreationQuestion.generateNew();
    return ok(views.html.boolcreatequestion.render(UserManagement.getCurrentUser(), question));
  }

  public Result newBoolFilloutQuestion() {
    FilloutQuestion question = FilloutQuestion.generateNew();
    return ok(views.html.boolfilloutquestion.render(UserManagement.getCurrentUser(), question));
  }
}
