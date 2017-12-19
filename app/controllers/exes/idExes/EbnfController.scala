package controllers.exes.idExes

import javax.inject._

import model.core._
import model.ebnf.EbnfConsts._
import model.ebnf._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import play.twirl.api.Html
import views.html.ebnf._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

@Singleton
class EbnfController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[EbnfExercise, EbnfResult, GenericCompleteResult[EbnfResult]](cc, dbcp, r, EbnfToolObject) with JsonFormat {

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
      case (((terminals, variables), startSymbol), rules) => Grammar(terminals, variables, startSymbol, rules)
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

  override def correctEx(user: User, grammar: Grammar, exercise: EbnfCompleteExercise): Try[GenericCompleteResult[EbnfResult]] = {
    // FIXME: implement!
    val derived = grammar.deriveAll

    println(derived)

    Failure(new Exception("Not yet implemented..."))
  }

  // Views

  override def renderEditRest(exercise: Option[EbnfCompleteExercise]): Html = new Html(
    s"""|<div class="form-group">
        |  <label for="$TERMINALS">Terminalsymbole:</label>
        |  <input class="form-control" name="$TERMINALS" id="$TERMINALS" placeholder="Terminalsymbole, durch Kommata getrennt"
        |         value="${exercise map (_.ex.joinedTerminals) getOrElse ""}" required>
        |</div>""".stripMargin)

  override def renderExercise(user: User, exercise: EbnfCompleteExercise): Html = ebnfExercise(user, exercise.ex)

  override def renderResult(correctionResult: GenericCompleteResult[EbnfResult]): Html = new Html("") //ebnfResult(correctionResult)

  //  override def renderExesListRest: Html = new Html("")

}