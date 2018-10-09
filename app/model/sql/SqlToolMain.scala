package model.sql

import javax.inject._
import model._
import model.core.Java_Levenshtein
import model.core.result.EvaluationResult
import model.sql.SqlToolMain._
import model.toolMains.{CollectionToolMain, ToolList, ToolState}
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

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

  def findBestFittingSample(userSt: String, samples: List[SqlSample]): SqlSample = samples.minBy(samp => Java_Levenshtein.levenshteinDistance(samp.sample, userSt))

  def allDaos: Seq[SqlExecutionDAO] = correctorsAndDaos.values.map(_._2).toSet.toSeq

}

@Singleton
class SqlToolMain @Inject()(override val tables: SqlTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Sql", "sql") {

  // Abstract types

  override type ExType = SqlExercise

  override type CompExType = SqlCompleteEx

  override type CollType = SqlScenario

  override type CompCollType = SqlCompleteScenario

  override type Tables = SqlTableDefs

  override type SolType = String

  override type DBSolType = SqlSolution

  override type R = EvaluationResult

  override type CompResult = SqlCorrResult

  // Members

  override val collectionSingularName: String = "Szenario"
  override val collectionPluralName  : String = "Szenarien"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = SqlConsts

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[SqlCompleteScenario] = SqlYamlProtocol.SqlScenarioYamlFormat

  // db

  override def futureSaveRead(exercises: Seq[CompCollType]): Future[Seq[(CompCollType, Boolean)]] = Future.sequence(exercises map {
    compColl =>
      val scriptFilePath = exerciseResourcesFolder / s"${compColl.coll.shortName}.sql"

      allDaos.foreach(_.executeSetup(compColl.coll.shortName, scriptFilePath))

      tables.saveCompleteColl(compColl) map (saveRes => (compColl, saveRes))
  })

  override protected def saveSolution(solution: SqlSolution): Future[Boolean] = tables.saveSolution(solution)

  // Read from requests

  override protected def readSolution(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap {
      case JsString(value) => Some(value)
      case _               => None
    }

  override protected def compExTypeForm(collId: Int): Form[SqlCompleteEx] = SqlFormMappings.sqlCompleteExForm(collId)

  // Views

  override def adminRenderEditRest(collOpt: Option[SqlCompleteScenario]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${SqlConsts.shortNameName}">Name der DB:</label>
       |    <input class="form-control" name="${SqlConsts.shortNameName}" id="${SqlConsts.shortNameName}" required ${collOpt map (coll => s"""value="${coll.coll.shortName}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  override def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlCompleteEx, numOfExes: Int, maybeOldSolution: Option[DBSolType])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {

    val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val oldOrDefSol = maybeOldSolution map (_.solution) getOrElse ""

    views.html.collectionExercises.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, numOfExes, this)
  }

  override def renderExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.collectionExercises.sql.editSqlExercise(user, newEx, isCreation, this, toolList)

  // FIXME: remove this method...
  override def renderEditRest(exercise: SqlCompleteEx): Html = ???

  // Correction

  override protected def correctEx(user: User, learnerSolution: SolType, sqlScenario: SqlScenario, exercise: SqlCompleteEx): Future[Try[SqlCorrResult]] = Future {
    //    saveSolution(learnerSolution) map { solutionSaved =>

    correctorsAndDaos.get(exercise.ex.exerciseType) match {
      case None                   => Failure(new Exception("There is no corrector or sql dao for " + exercise.ex.exerciseType))
      case Some((corrector, dao)) =>
        // FIXME: parse queries here!?!

        val sample = findBestFittingSample(learnerSolution, exercise.samples.toList)
        Try(corrector.correct(dao, learnerSolution, sample, exercise, sqlScenario))
      //      }
    }
  }

  // Helper methods

  override def instantiateCollection(id: Int, state: ExerciseState): SqlCompleteScenario = SqlCompleteScenario(
    SqlScenario(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, shortName = ""), exercises = Seq[SqlCompleteEx]())

  override def instantiateExercise(collId: Int, id: Int, state: ExerciseState): SqlCompleteEx = SqlCompleteEx(
    SqlExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, exerciseType = SqlExerciseType.SELECT,
      collectionId = collId, collSemVer = SemanticVersion(0, 1, 0), tags = "", hint = None), samples = Seq[SqlSample]())

  override def instantiateSolution(id: Int, username: String, coll: SqlScenario, exercise: SqlCompleteEx, solution: String,
                                   points: Points, maxPoints: Points): SqlSolution =
    SqlSolution(id, username, exercise.ex.id, exercise.ex.semanticVersion, coll.id, coll.semanticVersion, solution, points, maxPoints)

}
