package model.tools.collectionTools.regex

import javax.inject.Inject
import model._
import model.points._
import model.tools.ToolJsonProtocol
import model.tools.collectionTools.{CollectionToolMain, ExerciseCollection}
import model.tools.collectionTools.regex.persistence.RegexTableDefs
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class RegexToolMain @Inject()(override val tables: RegexTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain(RegexConsts) {

  override type PartType = RegexExPart
  override type ExType = RegexExercise

  override type SolType = String
  override type SampleSolType = StringSampleSolution
  override type UserSolType = StringUserSolution[RegexExPart]

  override type ReviewType = RegexExerciseReview

  override type ResultType = RegexEvalutationResult
  override type CompResultType = RegexCompleteResult

  override type Tables = RegexTableDefs

  // Members

  override val exParts  : Seq[RegexExPart] = RegexExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[RegexExercise, StringSampleSolution, RegexCompleteResult] =
    RegexToolJsonProtocol

  override protected val exerciseYamlFormat: YamlFormat[RegexExercise] = RegexToolYamlProtocol.regexExerciseYamlFormat

  override val exerciseReviewForm: Form[RegexExerciseReview] = RegexToolForm.exerciseReviewForm

  // Database helpers

  override protected def instantiateSolution(id: Int, exercise: RegexExercise, part: RegexExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[RegexExPart] =
    StringUserSolution[RegexExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: RegexCompleteResult, solSaved: Boolean): RegexCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: RegexExPart): Either[String, String] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(regex) => Right(regex)
      case other           => Left(s"Json was no string but $other")
    }
  }

  override protected def correctEx(user: User, sol: String, coll: ExerciseCollection, exercise: RegexExercise, part: RegexExPart): Future[Try[RegexCompleteResult]] =
    Future.successful(RegexCorrector.correct(sol, exercise))

  // Views

}
