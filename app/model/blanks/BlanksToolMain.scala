package model.blanks

import javax.inject._
import model._
import model.blanks.BlanksConsts._
import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.toolMains.IdExerciseToolMain
import model.yaml.MyYamlFormat
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

@Singleton
class BlanksToolMain @Inject()(val tables: BlanksTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("blanks") with JsonFormat {

  // Abstract types

  override type ExType = BlanksExercise

  override type CompExType = BlanksCompleteExercise

  override type Tables = BlanksTableDefs

  override type PartType = BlanksExPart

  override type SolType = Seq[BlanksAnswer]

  override type DBSolType = BlanksSolution

  override type R = MatchingResult[BlanksAnswer, GenericAnalysisResult, BlanksAnswerMatch]

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

  //  override def readSolutionFromPostRequest(user: User, id: Int, part: BlanksExPart)(implicit request: Request[AnyContent]): Option[BlanksSolution] = None

  override def readSolutionFromPutRequest(user: User, id: Int, part: BlanksExPart)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap (_.asArray(_.asObj flatMap { jsObj =>
      for {
        id <- jsObj.intField(idName)
        answer <- jsObj.stringField(valueName)
      } yield BlanksAnswer(id, -1, answer)
    }))

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: BlanksExPart): Option[SolType] = ???

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): BlanksCompleteExercise =
    BlanksCompleteExercise(BlanksExercise(id, title = "", author = "", text = "", state, SemanticVersion(0, 1, 0), rawBlanksText = "", blanksText = ""), samples = Seq.empty)

  override def instantiateSolution(username: String, exerciseId: Int, part: BlanksExPart, solution: Seq[BlanksAnswer], points: Double, maxPoints: Double): BlanksSolution =
    BlanksSolution(username, exerciseId, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[BlanksCompleteExercise] = BlanksYamlProtocol.BlanksYamlFormat

  // Correction

  override protected def correctEx(user: User, sol: Seq[BlanksAnswer], exercise: BlanksCompleteExercise, part: BlanksExPart): Future[Try[BlanksCompleteResult]] =
    Future(Try(BlanksCompleteResult(sol, BlanksCorrector.doMatch(sol, exercise.samples))))

  override def futureSampleSolutionForExerciseAndPart(id: Int, partStr: BlanksExPart): Future[String] = ???

  // Views

  override def renderExercise(user: User, exercise: BlanksCompleteExercise, part: BlanksExPart, oldSolution: Option[BlanksSolution]): Html =
    views.html.idExercises.blanks.blanksExercise(user, exercise)

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

  override def onLiveCorrectionResult(pointsSaved: Boolean, result: BlanksCompleteResult): JsValue = ???

  //    JsArray(
  //    result.result.allMatches map (m => Json.obj(
  //      idName -> JsNumber(BigDecimal(m.userArg map (_.id) getOrElse -1)),
  //      correctnessName -> m.matchType.entryName,
  //      explanationName -> m.explanations))
  //  )

}

