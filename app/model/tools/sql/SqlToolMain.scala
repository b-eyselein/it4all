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
import play.api.Logger

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

  private val logger = Logger(classOf[SqlToolMain])

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

  override val exParts: IndexedSeq[SqlExPart] = SqlExParts.values

  // Yaml, Html forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[SqlScenario] = SqlYamlProtocol.SqlCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[SqlExercise] = SqlYamlProtocol.SqlExerciseYamlFormat

  override val collectionForm    : Form[SqlScenario]       = SqlToolForms.collectionFormat
  override val exerciseForm      : Form[SqlExercise]       = SqlToolForms.exerciseFormat
  override val exerciseReviewForm: Form[SqlExerciseReview] = SqlToolForms.exerciseReviewForm

  override val sampleSolutionJsonFormat: Format[StringSampleSolution] = StringSampleSolutionJsonProtocol.stringSampleSolutionJsonFormat

  override val completeResultJsonProtocol: CompleteResultJsonProtocol[EvaluationResult, SqlCorrResult] = SqlJsonProtocols

  // Views

  override def previewExerciseRest(ex: Exercise): Html = ex match {
    case se: SqlExercise => views.html.toolViews.sql.previewSqlExerciseRest(se)
    case _               =>
      logger.error(s"ERROR: Tries to preview a ${ex.getClass} with SqlToolMain")
      Html("")
  }

  override def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlExercise, part: SqlExPart, maybeOldSolution: Option[UserSolType])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {

    val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val oldOrDefSol = maybeOldSolution.map(_.solution).getOrElse("")

    views.html.toolViews.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, this)
  }

  override def renderExerciseEditForm(user: User, collId: Int, newEx: ExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.toolViews.sql.editSqlExercise(user, collId, newEx, isCreation, this, toolList)

  // FIXME: remove this method...
  override def renderEditRest(exercise: SqlExercise): Html = ???

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: SqlExPart): Either[String, SolType] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(value) => Right(value)
      case other           => Left(s"Json was no string but ${other}")
    }
  }

  override protected def correctEx(user: User, learnerSolution: SolType, sqlScenario: SqlScenario, exercise: SqlExercise, part: SqlExPart): Future[Try[SqlCorrResult]] =
    correctorsAndDaos.get(exercise.exerciseType) match {
      case None                   => Future.successful(Failure(new Exception(s"There is no corrector or sql dao for ${exercise.exerciseType}")))
      case Some((corrector, dao)) => corrector.correct(dao, learnerSolution, exercise.samples, exercise, sqlScenario)
    }

  // Other helper methods

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): SqlScenario =
    SqlScenario(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): SqlExercise = SqlExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author = "", text = "", state, exerciseType = SqlExerciseType.SELECT,
    tags = Seq[SqlExTag](), hint = None, samples = Seq[StringSampleSolution]()
  )

  override protected def instantiateSolution(id: Int, exercise: SqlExercise, part: SqlExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[SqlExPart] =
    StringUserSolution[SqlExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: SqlCorrResult, solSaved: Boolean): SqlCorrResult = compResult match {
    case sr: SqlResult      => sr.copy(solutionSaved = solSaved)
    case sf: SqlParseFailed => sf.copy(solutionSaved = solSaved)
  }

}
