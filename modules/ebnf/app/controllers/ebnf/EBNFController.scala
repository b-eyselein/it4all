package controllers.ebnf

import javax.inject.Inject

import controllers.core.IdExController
import model.Secured
import model.ebnf.{EBNFExercise, EBNFResult}
import model.result.CompleteResult
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.mvc.Security.Authenticated
import play.twirl.api.Html

import scala.util.Try

@Authenticated(classOf[Secured])
class EBNFController @Inject()(f: FormFactory)
  extends IdExController[EBNFExercise, EBNFResult](f, EBNFExercise.finder, EBNFToolObject) {

  override def correctEx(form: DynamicForm, exercise: EBNFExercise, user: User): Try[CompleteResult[EBNFResult]] = ??? // FIXME


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

  override def renderExercise(user: User, exercise: EBNFExercise): Html = views.html.ebnfExercise.render(user, exercise)

  override def renderResult(correctionResult: CompleteResult[EBNFResult]): Html = views.html.ebnfResult.render(correctionResult)

  override def renderExesListRest = new Html("")

}