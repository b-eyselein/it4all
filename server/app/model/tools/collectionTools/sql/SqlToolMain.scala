package model.tools.collectionTools.sql

import model.points.Points
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, StringSampleSolutionToolJsonProtocol}
import model.{StringUserSolution, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.libs.json._

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}


object SqlToolMain extends CollectionToolMain(SqlConsts) {

  val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> ((SelectCorrector, SelectDAO)),
    SqlExerciseType.CREATE -> ((CreateCorrector, CreateDAO)),
    SqlExerciseType.UPDATE -> ((UpdateCorrector, ChangeDAO)),
    SqlExerciseType.INSERT -> ((InsertCorrector, ChangeDAO)),
    SqlExerciseType.DELETE -> ((DeleteCorrector, ChangeDAO))
  )

  def allDaos: Seq[SqlExecutionDAO] = correctorsAndDaos.values.map(_._2).toSet.toSeq

  // Abstract types

  override type PartType = SqlExPart
  override type ExContentType = SqlExerciseContent

  override type SolType = String
  override type UserSolType = StringUserSolution[SqlExPart]

  override type CompResultType = SqlCorrResult


  // Members

  override val exParts: IndexedSeq[SqlExPart] = SqlExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[SqlExPart, SqlExerciseContent, SqlCorrResult] =
    SqlJsonProtocols

  override protected val exerciseContentYamlFormat: YamlFormat[SqlExerciseContent] = SqlYamlProtocol.sqlExerciseYamlFormat

  // Correction

  override protected def readSolution(jsValue: JsValue, part: SqlExPart): Either[String, SolType] = jsValue match {
    case JsString(value) => Right(value)
    case other           => Left(s"Json was no string but ${other}")
  }

  override protected def correctEx(
    user: User, learnerSolution: SolType, sqlScenario: ExerciseCollection, exercise: Exercise, content: SqlExerciseContent, part: SqlExPart
  )(implicit executionContext: ExecutionContext): Future[Try[SqlCorrResult]] = correctorsAndDaos.get(content.exerciseType) match {
    case None                   => Future.successful(Failure(new Exception(s"There is no corrector or sql dao for ${content.exerciseType}")))
    case Some((corrector, dao)) => corrector.correct(dao, learnerSolution, content, sqlScenario)
  }

  // Other helper methods

  override protected def instantiateSolution(id: Int, exercise: Exercise, part: SqlExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[SqlExPart] =
    StringUserSolution[SqlExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: SqlCorrResult, solSaved: Boolean): SqlCorrResult = compResult match {
    case sr: SqlResult      => sr.copy(solutionSaved = solSaved)
    case sf: SqlParseFailed => sf.copy(solutionSaved = solSaved)
  }

}
