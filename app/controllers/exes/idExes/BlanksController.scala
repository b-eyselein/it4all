package controllers.exes.idExes

import javax.inject._

import controllers.Secured
import controllers.exes.IntExIdentifier
import model.blanks._
import model.core._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json
import play.api.libs.json._
import play.api.mvc.{AnyContent, ControllerComponents, Request, Result}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

@Singleton
class BlanksController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[BlanksExercise, BlanksAnswerMatchingResult, BlanksCompleteResult](cc, dbcp, r, BlanksToolObject) with JsonFormat with Secured {

  // Reading solution from requests

  override type SolType = Seq[BlanksAnswer]

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[Seq[BlanksAnswer]] = None

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[Seq[BlanksAnswer]] = request.body.asJson flatMap (_.asArray(jsValue => {
    val id = jsValue.asObj flatMap (_.intField("id")) getOrElse -1
    val answer = jsValue.asObj flatMap (_.stringField("value")) getOrElse ""

    BlanksAnswer(id, -1, answer)
  }))


  // Yaml

  override type CompEx = BlanksCompleteExercise

  override val yamlFormat: YamlFormat[BlanksCompleteExercise] = BlanksYamlProtocol.BlanksYamlFormat

  // db

  override type TQ = repo.BlanksExercisesTable

  override def tq = repo.blanksExercises

  override protected def futureCompleteExById(id: Int): Future[Option[BlanksCompleteExercise]] = repo.blanksCompleteExById(id)

  override protected def futureCompleteExes: Future[Seq[BlanksCompleteExercise]] = repo.blanksCompleteExes

  override protected def saveRead(read: Seq[BlanksCompleteExercise]): Future[Seq[Any]] = Future.sequence(read map repo.saveBlanksExercise)

  // Correction

  override protected def correctEx(user: User, sol: Seq[BlanksAnswer], exercise: BlanksCompleteExercise, identifier: IntExIdentifier): Try[BlanksCompleteResult] =
    Try(BlanksCompleteResult(sol, BlanksCorrector.doMatch(sol, exercise.samples)))

  // Views

  override def renderExercise(user: User, exercise: BlanksCompleteExercise): Future[Html] = Future(views.html.blanks.blanksExercise(user, exercise))

  override def renderResult(correctionResult: BlanksCompleteResult): Html = correctionResult.result.describe // ???

  override def renderEditRest(exercise: Option[BlanksCompleteExercise]): Html = new Html(
    s"""<div class="form-group">
       |  <label class="control-label" for="blankstext">Lückentext:</label>
       |  <textarea rows="5" class="form-control" id="blankstext" name="blankstext">${exercise map (_.ex.blanksText) getOrElse ""}</textarea>
       |</div>
       |<div class="form-group">
       |  <label class="control-label">Lösungen:</label>
       |  <ul>${exercise map (_.samples map renderSample mkString) getOrElse "<li>--</li>"}</ul>
       |</div>""".stripMargin)

  private def renderSample(sample: BlanksAnswer): String = s"<li>${sample.id} &rarr; ${sample.solution}</li>"

  override protected def onLiveCorrectionSuccess(correctionResult: BlanksCompleteResult): Result = Ok(json.JsArray(
    correctionResult.result.allMatches map (m => Json.obj(
      "id" -> JsNumber(BigDecimal(m.userArg map (_.id) getOrElse -1)),
      "correctness" -> m.matchType.name,
      "explanation" -> m.explanation))
  ))

}

