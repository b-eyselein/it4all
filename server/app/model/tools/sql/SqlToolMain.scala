package model.tools.sql

import javax.inject.{Inject, Singleton}
import model._
import model.core.result.EvaluationResult
import model.points.Points
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.ToolJsonProtocol
import model.tools.sql.SqlToolMain._
import model.tools.sql.persistence.SqlTableDefs
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

object SqlToolMain {

  val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> ((SelectCorrector, SelectDAO)),
    SqlExerciseType.CREATE -> ((CreateCorrector, CreateDAO)),
    SqlExerciseType.UPDATE -> ((UpdateCorrector, ChangeDAO)),
    SqlExerciseType.INSERT -> ((InsertCorrector, ChangeDAO)),
    SqlExerciseType.DELETE -> ((DeleteCorrector, ChangeDAO))
  )

  def allDaos: Seq[SqlExecutionDAO] = correctorsAndDaos.values.map(_._2).toSet.toSeq

}

@Singleton
class SqlToolMain @Inject()(override val tables: SqlTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Sql", "sql") {

  // Abstract types

  override type PartType = SqlExPart
  override type ExType = SqlExercise

  override type SolType = String
  override type SampleSolType = StringSampleSolution
  override type UserSolType = StringUserSolution[SqlExPart]

  override type ReviewType = SqlExerciseReview

  override type ResultType = EvaluationResult
  override type CompResultType = SqlCorrResult

  override type Tables = SqlTableDefs

  // Members

  override val toolState: ToolState = ToolState.LIVE

  override val exParts: IndexedSeq[SqlExPart] = SqlExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[SqlExercise, StringSampleSolution, SqlCorrResult] = SqlJsonProtocols

  override protected val exerciseYamlFormat: YamlFormat[SqlExercise] = SqlYamlProtocol.sqlExerciseYamlFormat

  override val exerciseReviewForm: Form[SqlExerciseReview] = SqlToolForms.exerciseReviewForm


  // Correction

  override protected def readSolution(request: Request[AnyContent], part: SqlExPart): Either[String, SolType] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(value) => Right(value)
      case other           => Left(s"Json was no string but ${other}")
    }
  }

  override protected def correctEx(user: User, learnerSolution: SolType, sqlScenario: ExerciseCollection, exercise: SqlExercise, part: SqlExPart): Future[Try[SqlCorrResult]] =
    correctorsAndDaos.get(exercise.exerciseType) match {
      case None                   => Future.successful(Failure(new Exception(s"There is no corrector or sql dao for ${exercise.exerciseType}")))
      case Some((corrector, dao)) => corrector.correct(dao, learnerSolution, exercise.samples, exercise, sqlScenario)
    }

  // Other helper methods

  override protected def instantiateSolution(id: Int, exercise: SqlExercise, part: SqlExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[SqlExPart] =
    StringUserSolution[SqlExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: SqlCorrResult, solSaved: Boolean): SqlCorrResult = compResult match {
    case sr: SqlResult      => sr.copy(solutionSaved = solSaved)
    case sf: SqlParseFailed => sf.copy(solutionSaved = solSaved)
  }

}
