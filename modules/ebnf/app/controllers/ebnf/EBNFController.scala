package controllers.ebnf

import controllers.core.BaseController
import controllers.core.IdExController
import javax.inject.Inject
import model.Secured
import model.ebnf.EBNFExercise
import model.ebnf.Grammar
import model.ebnf.RuleParser
import model.ebnf.Variable
import model.ebnf.EBNFCompleteResult
import model.result.CompleteResult
import model.result.EvaluationResult
import model.user.User
import play.data.DynamicForm
import play.data.FormFactory
import play.mvc.Controller
import play.mvc.Result
import play.mvc.Results
import play.twirl.api.Html
import play.mvc.Security.Authenticated
import model.ebnf.EBNFResult

import scala.collection.JavaConverters._
import scala.util.Failure

@Authenticated(classOf[Secured])
class EBNFController @Inject() (f: FormFactory)
  extends IdExController[EBNFExercise, EBNFResult](f, "ebnf", EBNFExercise.finder, EBNFToolObject) {

  override def correctEx(form: DynamicForm, exercise: EBNFExercise, user: User) = // FIXME
    ???
  //    val data = Controller.request.body.asFormUrlEncoded
  //
  //    val terminals = data.get("terminals")(0).split(" ").map(str => new TerminalSymbol(str.replace("'", ""))).toList
  //    val variables = data.get("variables")(0).split(",").map(new Variable(_)).toList
  //    val start = new Variable(data.get("start")(0))
  //    val replacementsAsStrs = data.get("rule[]")
  //
  //    System.out.println(replacementsAsStrs.toList.mkString("\n"))
  //
  //    val replacements = replacementsAsStrs.map(RuleParser.parseReplacement(_))
  //    val rules = replacements.map(new Rule(Variable("S"), _)).toList
  //
  //    val grammar = new Grammar(terminals, variables, start, rules)
  //
  //    System.out.println(grammar)
  //
  //    new EBNFCompleteResult(new EBNFResult(grammar))

  override def renderExercise(user: User, exercise: EBNFExercise) = views.html.ebnfExercise.render(user, exercise)

  override def renderResult(correctionResult: CompleteResult[EBNFResult]) = views.html.ebnfResult.render(correctionResult)

  override def renderExesListRest = new Html("");

}