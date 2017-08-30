package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.CorrectionException;
import model.Secured;
import model.ebnf.EBNFExercise;
import model.ebnf.EBNFExercise$;
import model.ebnf.Grammar;
import model.ebnf.Rule;
import model.ebnf.RuleParser$;
import model.ebnf.TerminalSymbol;
import model.ebnf.Variable;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;

@Authenticated(Secured.class)
public class EBNFController extends ExerciseController<EBNFExercise, EvaluationResult> {

  @Inject
  public EBNFController(FormFactory factory) {
    super(factory, "ebnf", EBNFExercise$.MODULE$.finder());
  }

  public Result index() {
    return ok(views.html.ebnf.ebnfIndex.render(getUser(), finder.all()));
  }

  @Override
  protected CompleteResult<EvaluationResult> correct(DynamicForm form, EBNFExercise exercise, User user)
      throws CorrectionException {
    Map<String, String[]> data = request().body().asFormUrlEncoded();

    List<TerminalSymbol> terminals = Arrays.stream(data.get("terminals")[0].split(",")).map(TerminalSymbol::new)
        .collect(Collectors.toList());
    List<Variable> variables = Arrays.stream(data.get("variables")[0].split(",")).map(Variable::new)
        .collect(Collectors.toList());
    Variable start = new Variable(data.get("start")[0]);
    String[] rulesAsString = data.get("rule[]");
    System.out.println(rulesAsString);
    List<Rule> rules = Arrays.stream(rulesAsString).map(RuleParser$.MODULE$::parse).collect(Collectors.toList());

    Grammar grammar = new Grammar(terminals, variables, start, rules);

    System.out.println(grammar);

    return null;
  }

  @Override
  protected Html renderExercise(User user, EBNFExercise exercise) {
    return views.html.ebnf.ebnfExercise.render(user, exercise);
  }

  @Override
  protected Html renderResult(CompleteResult<EvaluationResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }

}