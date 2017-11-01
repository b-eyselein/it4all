package controllers.ebnf

import javax.inject._

import controllers.core.AIdExController
import model.User
import model.core.StringConsts._
import model.core._
import model.core.result.CompleteResult
import model.ebnf.{EbnfExercise, EbnfExerciseReads, EbnfResult}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Reads
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try


@Singleton
class EbnfController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[EbnfExercise, EbnfResult](cc, dbcp, r, EbnfToolObject) {

  override type TQ = repo.EbnfExerciseTable

  override def reads: Reads[EbnfExercise] = EbnfExerciseReads.ebnfExReads

  override def tq = repo.ebnfExercises

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  // Admin

  override def renderEditRest(exercise: Option[EbnfExercise]): Html = new Html(
    s"""|<div class="form-group">
        |  <label for="$TERMINALS">Terminalsymbole:</label>
        |  <input class="form-control" name="$TERMINALS" id="$TERMINALS" placeholder="Terminalsymbole, durch Kommata getrennt"
        |         ${exercise.map(_.terminals).getOrElse("")} required>
        |</div>""".stripMargin)

  // User

  override def correctEx(sol: StringSolution, exercise: Option[EbnfExercise], user: User): Try[CompleteResult[EbnfResult]] = ??? // FIXME


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
  //    new EbnfCompleteResult(new EbnfResult(grammar))

  override def renderExercise(user: User, exercise: EbnfExercise): Html = views.html.ebnf.ebnfExercise.render(user, exercise)

  override def renderResult(correctionResult: CompleteResult[EbnfResult]): Html = new Html("") //views.html.ebnf.ebnfResult.render(correctionResult)

  override def renderExesListRest = new Html("")

}