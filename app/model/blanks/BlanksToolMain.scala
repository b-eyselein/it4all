package model.blanks

import javax.inject._
import model.blanks.BlanksConsts._
import model.blanks.BlanksExParts.BlanksExPart
import model.toolMains.AExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import play.api.data.Form
import play.api.libs.json
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

@Singleton
class BlanksToolMain @Inject()(val tables: BlanksTableDefs)(implicit ec: ExecutionContext) extends AExerciseToolMain("blanks") with JsonFormat {

  // Abstract types

  override type ExType = BlanksExercise

  override type CompExType = BlanksCompleteExercise

  override type Tables = BlanksTableDefs

  override type PartType = BlanksExPart

  override type SolType = BlanksSolution

  override type R = BlanksAnswerMatchingResult

  override type CompResult = BlanksCompleteResult

  // Other members

  override val toolname: String = "Lückentext"

  override val consts: Consts = BlanksConsts

  override val exParts: Seq[BlanksExPart] = BlanksExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[BlanksExercise] = null
  //    Form(
  //    mapping(
  //
  //    )
  //  )

  // Reading solution from requests

  override def futureSaveSolution(sol: BlanksSolution): Future[Boolean] = ???

  override def readSolutionFromPostRequest(user: User, id: Int, part: BlanksExPart)(implicit request: Request[AnyContent]): Option[BlanksSolution] = None

  override def readSolutionFromPutRequest(user: User, id: Int, part: BlanksExPart)(implicit request: Request[AnyContent]): Option[BlanksSolution] =
    request.body.asJson flatMap (_.asArray(_.asObj flatMap { jsObj =>
      for {
        id <- jsObj.intField(idName)
        answer <- jsObj.stringField(valueName)
      } yield BlanksAnswer(id, -1, answer)
    })) map (answers => BlanksSolution(user.username, id, part, answers))

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: BlanksExPart): Option[BlanksSolution] = ???

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): BlanksCompleteExercise =
    BlanksCompleteExercise(BlanksExercise(id, title = "", author = "", text = "", state, rawBlanksText = "", blanksText = ""), samples = Seq.empty)

  // Yaml

  override val yamlFormat: MyYamlFormat[BlanksCompleteExercise] = BlanksYamlProtocol.BlanksYamlFormat

  // Correction

  override protected def correctEx(user: User, sol: BlanksSolution, exercise: BlanksCompleteExercise): Future[Try[BlanksCompleteResult]] =
    Future(Try(BlanksCompleteResult(sol.answers, BlanksCorrector.doMatch(sol.answers, exercise.samples))))

  // Views

  override def renderExercise(user: User, exercise: BlanksCompleteExercise, part: BlanksExPart, oldSolution: Option[BlanksSolution]): Html = views.html.blanks.blanksExercise(user, exercise)

  private def renderResult(correctionResult: BlanksCompleteResult): Html = Html(correctionResult.result.describe) // ???

  override def renderEditRest(exercise: BlanksCompleteExercise): Html = new Html(
    s"""<div class="form-group">
       |  <label class="control-label" for="blankstext">Lückentext:</label>
       |  <textarea rows="5" class="form-control" id="blankstext" name="blankstext">${exercise.ex.blanksText}</textarea>
       |</div>
       |<div class="form-group">
       |  <label class="control-label">Lösungen:</label>
       |  <ul>${exercise.samples map renderSample mkString}</ul>
       |</div>""".stripMargin)

  private def renderSample(sample: BlanksAnswer): String = s"<li>${sample.id} &rarr; ${sample.solution}</li>"

  override def onSubmitCorrectionResult(user: User, result: BlanksCompleteResult): Html = ???

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: BlanksCompleteResult): JsValue = json.JsArray(
    result.result.allMatches map (m => Json.obj(
      idName -> JsNumber(BigDecimal(m.userArg map (_.id) getOrElse -1)),
      correctnessName -> m.matchType.name,
      explanationName -> m.explanation))
  )

}

