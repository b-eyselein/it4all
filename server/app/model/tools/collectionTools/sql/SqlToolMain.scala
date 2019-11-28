package model.tools.collectionTools.sql

import model.User
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, StringSampleSolutionToolJsonProtocol}
import net.jcazevedo.moultingyaml.YamlFormat

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}


object SqlToolMain extends CollectionToolMain(SqlConsts) {

  private val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> ((SelectCorrector, SelectDAO)),
    SqlExerciseType.CREATE -> ((CreateCorrector, CreateDAO)),
    SqlExerciseType.UPDATE -> ((UpdateCorrector, ChangeDAO)),
    SqlExerciseType.INSERT -> ((InsertCorrector, ChangeDAO)),
    SqlExerciseType.DELETE -> ((DeleteCorrector, ChangeDAO))
  )

  // Abstract types

  override type PartType = SqlExPart
  override type ExContentType = SqlExerciseContent
  override type SolType = String
  override type CompResultType = SqlCorrResult

  // Members

  override val exParts: IndexedSeq[SqlExPart] = SqlExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlCorrResult] =
    SqlJsonProtocols

  override protected val exerciseContentYamlFormat: YamlFormat[SqlExerciseContent] = SqlYamlProtocol.sqlExerciseYamlFormat

  // Correction

  override protected def correctEx(
    user: User,
    learnerSolution: SolType,
    sqlScenario: ExerciseCollection,
    exercise: Exercise,
    content: SqlExerciseContent,
    part: SqlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[SqlCorrResult]] =
    correctorsAndDaos.get(content.exerciseType) match {
      case None                   => Future.successful(Failure(new Exception(s"There is no corrector or sql dao for ${content.exerciseType}")))
      case Some((corrector, dao)) => corrector.correct(dao, learnerSolution, content, sqlScenario, solutionSaved)
    }

}
