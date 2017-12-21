package controllers.exes.idExes

import javax.inject._

import controllers.exes.IntExIdentifier
import model.core._
import model.ebnf.EbnfConsts._
import model.ebnf._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.{AnyContent, ControllerComponents, Request, Result}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class EbnfController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[EbnfExercise, EbnfTestdataMatchingResult, EbnfCompleteResult](cc, dbcp, r, EbnfToolObject) with JsonFormat {

  // Reading solution from requests

  override type SolType = Grammar

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[SolType] = {
    //    Some(Grammar(terminals = List.empty, variables = List.empty, startSymbol = null, rules = Map.empty))
    None
  }

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[SolType] = request.body.asJson flatMap (_.asObj) flatMap { jsObj: JsObject =>
    val maybeTerminals = jsObj.arrayField("terminals", _.asStr map (_.replace("'", "")) map Terminal)
    val maybeVariables = jsObj.arrayField("variables", _.asStr map Variable)
    val maybeStartSymbol = jsObj.stringField("startSymbol") map Variable
    val maybeRules = jsObj.arrayField("rules", readRuleFromJsValue) map (_.toMap)

    (maybeTerminals zip maybeVariables zip maybeStartSymbol zip maybeRules).headOption map {
      case (((terminals, variables), startSymbol), rules) => Grammar(terminals, variables, startSymbol, RulesList(rules))
    }
  }

  private def readRuleFromJsValue(jsValue: JsValue): Option[(Variable, Replacement)] = jsValue.asObj flatMap { jsObj =>
    val maybeVariable = jsObj.stringField("symbol") map Variable
    val maybeReplacement = jsObj.stringField("rule") map RuleParser.parseRules

    (maybeVariable zip maybeReplacement).headOption
  }

  // Yaml

  override type CompEx = EbnfCompleteExercise

  override val yamlFormat: YamlFormat[EbnfCompleteExercise] = EbnfExerciseYamlProtocol.EbnfExerciseYamlFormat

  // db

  override type TQ = repo.EbnfExerciseTable

  override def tq = repo.ebnfExercises

  override def futureCompleteExes: Future[Seq[EbnfCompleteExercise]] = repo.ebnfCompleteExes

  override def futureCompleteExById(id: Int): Future[Option[EbnfCompleteExercise]] = repo.ebnfCompleteExById(id)

  override def saveRead(read: Seq[EbnfCompleteExercise]): Future[Seq[Any]] = Future.sequence(read map repo.ebnfSaveCompleteEx)

  // Correction

  override def correctEx(user: User, grammar: Grammar, exercise: EbnfCompleteExercise, identifier: IntExIdentifier): Try[EbnfCompleteResult] = Try {
    repo.saveEbnfSolution(user, exercise, grammar.rulesList)

    val derived = grammar.deriveAll map (EbnfTestData(-1, _))

    val evalResult = EbnfCorrector.doMatch(derived, exercise.testdata)

    EbnfCompleteResult(grammar, evalResult)
  }

  // Views

  override def renderEditRest(exercise: Option[EbnfCompleteExercise]): Html = new Html(
    s"""|<div class="form-group">
        |  <label for="$TERMINALS">Terminalsymbole:</label>
        |  <input class="form-control" name="$TERMINALS" id="$TERMINALS" placeholder="Terminalsymbole, durch Kommata getrennt"
        |         value="${exercise map (_.ex.joinedTerminals) getOrElse ""}" required>
        |</div>""".stripMargin)

  override def renderExercise(user: User, exercise: EbnfCompleteExercise): Future[Html] =
    repo.readEbnfSolution(user.username, exercise.id) map (maybeSolution => views.html.ebnf.ebnfExercise(user, exercise.ex, maybeSolution.map(_.solution)))

  override def renderResult(correctionResult: EbnfCompleteResult): Html = correctionResult.result.describe

  override protected def onLiveCorrectionSuccess(correctionResult: EbnfCompleteResult): Result = Ok(renderResult(correctionResult))

}