package model.sql

import javax.inject._
import model._
import model.core.result.EvaluationResult
import model.core.{Levenshtein, SolutionFormHelper}
import model.sql.SqlConsts._
import model.sql.SqlToolMain._
import model.toolMains.{CollectionToolMain, ToolState}
import model.yaml.MyYamlFormat
import play.api.data.Form
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

object SqlToolMain {

  val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> (SelectCorrector, SelectDAO),
    SqlExerciseType.CREATE -> (CreateCorrector, CreateDAO),
    SqlExerciseType.UPDATE -> (UpdateCorrector, ChangeDAO),
    SqlExerciseType.INSERT -> (InsertCorrector, ChangeDAO),
    SqlExerciseType.DELETE -> (DeleteCorrector, ChangeDAO)
  )

  def findBestFittingSample(userSt: String, samples: List[SqlSample]): SqlSample = samples.minBy(samp => Levenshtein.levenshteinDistance(samp.sample, userSt))

  def allDaos: Seq[SqlExecutionDAO] = correctorsAndDaos.values.map(_._2).toSet.toSeq

}

@Singleton
class SqlToolMain @Inject()(override val tables: SqlTableDefs)(implicit ec: ExecutionContext) extends CollectionToolMain("sql") with JsonFormat {

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

  override val toolname              : String = "Sql"
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

  override def readSolutionFromPutRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap (_.asObj) flatMap (jsObj => jsObj.stringField(learnerSolutionName)) // map (str => SqlSolution(user.username, collId, id, str))

  override def readSolutionFromPostRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType] =
    SolutionFormHelper.stringSolForm.bindFromRequest() fold(_ => None, sol => Some(sol.learnerSolution))

  override protected def compExTypeForm(collId: Int): Form[SqlCompleteEx] = SqlFormMappings.sqlCompleteExForm(collId)

  // Views

  override def adminRenderEditRest(collOpt: Option[SqlCompleteScenario]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${SqlConsts.shortNameName}">Name der DB:</label>
       |    <input class="form-control" name="${SqlConsts.shortNameName}" id="${SqlConsts.shortNameName}" required ${collOpt map (coll => s"""value="${coll.coll.shortName}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  override def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlCompleteEx, numOfExes: Int): Future[Html] =
    tables.futureMaybeOldSolution(user.username, sqlScenario.id, exercise.ex.id) map (_ map (_.solution) getOrElse "") map { oldOrDefSol =>

      val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

      views.html.collectionExercises.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, numOfExes, this)
    }

  override def renderExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean): Html =
    views.html.collectionExercises.sql.editSqlExercise(user, newEx, this, isCreation)

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

  // Result handlers

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ??? // FIXME: implement...

  override def onSubmitCorrectionResult(user: User, result: SqlCorrResult): Html = ???

  //    result match {
  //    case res: SqlResult           => views.html.core.correction(result, views.html.sql.sqlResult(res), user, this)
  //    case SqlParseFailed(_, error) =>
  // //           FIXME: implement...
  //      Logger.error("There has been a sql correction error", error)
  //      Html("Es gab einen Fehler bei der Korrektur!")
  //  }

  //  override def onLiveCorrectionResult(result: SqlCorrResult): JsValue = result match {
  //    case res: SqlResult              => res.toJson
  //    case SqlParseFailed(_, _, error) => Json.obj("msg" -> error.getMessage)
  //  }

  // Helper methods

  override def instantiateCollection(id: Int, state: ExerciseState): SqlCompleteScenario = SqlCompleteScenario(
    SqlScenario(id, title = "", author = "", text = "", state, shortName = ""), exercises = Seq.empty)

  override def instantiateExercise(collId: Int, id: Int, state: ExerciseState): SqlCompleteEx = SqlCompleteEx(
    SqlExercise(id, title = "", author = "", text = "", state, exerciseType = SqlExerciseType.SELECT, collectionId = collId, tags = "", hint = None), samples = Seq.empty)

  override def instantiateSolution(username: String, collId: Int, id: Int, solution: String, points: Double, maxPoints: Double): SqlSolution =
    SqlSolution(username, collId, id, solution, points, maxPoints)

}
