package model.tools.sql

import javax.inject.{Inject, Singleton}
import model._
import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.tools.sql.SqlToolMain._
import model.toolMains.{CollectionToolMain, ToolList, ToolState}
import model.tools.sql.persistence.SqlTableDefs
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html
import model.points.Points

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
  override type CollType = SqlScenario

  override type SolType = String
  override type SampleSolType = StringSampleSolution
  override type UserSolType = StringUserSolution[SqlExPart]

  override type ReviewType = SqlExerciseReview

  override type ResultType = EvaluationResult
  override type CompResultType = SqlCorrResult

  override type Tables = SqlTableDefs

  // Members

  override val toolState: ToolState = ToolState.LIVE

  override val usersCanCreateExes: Boolean = false

  override val exParts: IndexedSeq[SqlExPart] = SqlExParts.values

  // Yaml, Html forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[SqlScenario] = NewSqlYamlProtocol.SqlCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[SqlExercise] = NewSqlYamlProtocol.SqlExerciseYamlFormat

  override val collectionForm    : Form[SqlScenario]       = SqlToolForms.collectionFormat
  override val exerciseForm      : Form[SqlExercise]       = SqlToolForms.exerciseFormat
  override val exerciseReviewForm: Form[SqlExerciseReview] = SqlToolForms.exerciseReviewForm

  override val completeResultJsonProtocol: CompleteResultJsonProtocol[EvaluationResult, SqlCorrResult] = SqlCorrResultJsonProtocol

  // Views

  override def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlExercise, part: SqlExPart, maybeOldSolution: Option[UserSolType])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {

    val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val oldOrDefSol = maybeOldSolution map (_.solution) getOrElse ""

    views.html.toolViews.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, this)
  }

  override def renderExerciseEditForm(user: User, collId: Int, newEx: ExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.toolViews.sql.editSqlExercise(user, collId, newEx, isCreation, this, toolList)

  // FIXME: remove this method...
  override def renderEditRest(exercise: SqlExercise): Html = ???

  // Correction

  override protected def readSolution(user: User, collection: SqlScenario, exercise: SqlExercise, part: SqlExPart)
                                     (implicit request: Request[AnyContent]): Option[SolType] = request.body.asJson flatMap {
    case JsString(value) => Some(value)
    case _               => None
  }

  override protected def correctEx(user: User, learnerSolution: SolType, sqlScenario: SqlScenario, exercise: SqlExercise, part: SqlExPart): Future[Try[SqlCorrResult]] =
    correctorsAndDaos.get(exercise.exerciseType) match {
      case None                   => Future.successful(Failure(new Exception(s"There is no corrector or sql dao for ${exercise.exerciseType}")))
      case Some((corrector, dao)) => corrector.correct(dao, learnerSolution, exercise.samples, exercise, sqlScenario)
    }

  // Other helper methods

  override protected def exerciseHasPart(exercise: SqlExercise, partType: SqlExPart): Boolean = true

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): SqlScenario =
    SqlScenario(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): SqlExercise = SqlExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author = "", text = "", state, exerciseType = SqlExerciseType.SELECT,
    tags = Seq[SqlExTag](), hint = None, samples = Seq[StringSampleSolution]()
  )

  override protected def instantiateSolution(id: Int, exercise: SqlExercise, part: SqlExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[SqlExPart] =
    StringUserSolution[SqlExPart](id, part, solution, points, maxPoints)

}
