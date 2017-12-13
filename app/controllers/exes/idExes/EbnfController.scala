package controllers.exes.idExes

import javax.inject._

import model.User
import model.core._
import model.ebnf.EbnfConsts._
import model.ebnf.{EbnfExercise, EbnfResult}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import play.twirl.api.Html
import views.html.ebnf._

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class EbnfController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[EbnfExercise, EbnfResult, GenericCompleteResult[EbnfResult]](cc, dbcp, r, EbnfToolObject) {

  // Reading solution from requests

  override type SolType = String

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  // Yaml

  override type CompEx = EbnfExercise

  override val yamlFormat: YamlFormat[EbnfExercise] = null

  // db

  override type TQ = repo.EbnfExerciseTable

  override def tq = repo.ebnfExercises

  // Correction

  override def correctEx(user: User, sol: String, exercise: EbnfExercise): Try[GenericCompleteResult[EbnfResult]] = {
    // FIXME: implement!

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
    ???
  }

  // Views

  override def renderEditRest(exercise: Option[EbnfExercise]): Html = new Html(
    s"""|<div class="form-group">
        |  <label for="$TERMINALS">Terminalsymbole:</label>
        |  <input class="form-control" name="$TERMINALS" id="$TERMINALS" placeholder="Terminalsymbole, durch Kommata getrennt"
        |         ${exercise.map(_.terminals).getOrElse("")} required>
        |</div>""".stripMargin)

  override def renderExercise(user: User, exercise: EbnfExercise): Html = ebnfExercise(user, exercise)

  override def renderResult(correctionResult: GenericCompleteResult[EbnfResult]): Html = new Html("") //ebnfResult(correctionResult)

  override def renderExesListRest: Html = new Html("")

}