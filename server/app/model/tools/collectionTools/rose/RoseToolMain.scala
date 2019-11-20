package model.tools.collectionTools.rose


import javax.inject.{Inject, Singleton}
import model._
import model.points.Points
import model.tools.ToolJsonProtocol
import model.tools.collectionTools.{CollectionToolMain, ExerciseCollection}
import model.tools.collectionTools.programming.ProgLanguages
import model.tools.collectionTools.rose.persistence.RoseTableDefs
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

@Singleton
class RoseToolMain @Inject()(val tables: RoseTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain(RoseConsts) {

  // Abstract types

  override type PartType = RoseExPart
  override type ExType = RoseExercise

  override type SolType = String
  override type SampleSolType = RoseSampleSolution
  override type UserSolType = RoseUserSolution

  override type ReviewType = RoseExerciseReview

  override type ResultType = RoseExecutionResult

  override type CompResultType = RoseCompleteResult

  override type Tables = RoseTableDefs

  // Other members

  override val exParts  : Seq[RoseExPart] = RoseExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[RoseExercise, RoseSampleSolution, RoseCompleteResult] =
    RoseToolJsonProtocol

  override protected val exerciseYamlFormat: YamlFormat[RoseExercise] = RoseExYamlProtocol.roseExerciseYamlFormat

  override val exerciseReviewForm: Form[RoseExerciseReview] = RoseToolForms.exerciseReviewForm


  // Other helper methods

  override def instantiateSolution(id: Int, exercise: RoseExercise, part: RoseExPart, solution: String, points: Points, maxPoints: Points): RoseUserSolution =
    RoseUserSolution(id, part, language = ProgLanguages.StandardLanguage, solution, points, maxPoints)

  override def updateSolSaved(compResult: RoseCompleteResult, solSaved: Boolean): RoseCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: RoseExPart): Either[String, String] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(solution) => Right(solution)
      case _                  => Left("Request body is no string!")
    }
  }

  override protected def correctEx(user: User, sol: String, collection: ExerciseCollection, exercise: RoseExercise, part: RoseExPart): Future[Try[RoseCompleteResult]] =
    exercise.sampleSolutions.headOption match {
      case None                 => Future.successful(Failure(new Exception("No sample solution could be found!")))
      case Some(sampleSolution) =>

        RoseCorrector.correct(
          user, exercise, sol, sampleSolution.sample, ProgLanguages.StandardLanguage, solutionDirForExercise(user.username, collection.id, exercise.id)
        )
    }
}
