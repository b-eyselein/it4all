package model.sql

import javax.inject._
import model.Enums.ToolState
import model._
import model.core._
import model.sql.SqlConsts._
import model.sql.SqlEnums.SqlExerciseType._
import model.sql.SqlToolMain._
import model.toolMains.CollectionToolMain
import model.yaml.MyYamlFormat
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import views.html.sql._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

object SqlToolMain {

  val daos = Map(
    SELECT -> SelectDAO, CREATE -> CreateDAO, UPDATE -> ChangeDAO, INSERT -> ChangeDAO, DELETE -> ChangeDAO
  )

  val correctors = Map(
    CREATE -> CreateCorrector, DELETE -> DeleteCorrector, INSERT -> InsertCorrector, SELECT -> SelectCorrector, UPDATE -> UpdateCorrector
  )

  def findBestFittingSample(userSt: String, samples: List[SqlSample]): SqlSample = samples.minBy(samp => Levenshtein.levenshteinDistance(samp.sample, userSt))

}

@Singleton
class SqlToolMain @Inject()(override val tables: SqlTableDefs)(implicit ec: ExecutionContext) extends CollectionToolMain("sql") with JsonFormat {

  // Abstract types

  override type ExType = SqlExercise

  override type CompExType = SqlCompleteEx

  override type CollType = SqlScenario

  override type CompCollType = SqlCompleteScenario

  override type Tables = SqlTableDefs

  override type SolType = SqlSolution

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

  override def futureSaveRead(exercises: Seq[CompCollType])(implicit ec: ExecutionContext): Future[Seq[(CompCollType, Boolean)]] = Future.sequence(exercises map {
    compColl =>
      val scriptFilePath = exerciseResourcesFolder / s"${compColl.coll.shortName}.sql"

      daos.values.toList.distinct foreach (_.executeSetup(compColl.coll.shortName, scriptFilePath))

      tables.saveCompleteColl(compColl) map (saveRes => (compColl, saveRes))
  })

  override protected def saveSolution(solution: SqlSolution)(implicit ec: ExecutionContext): Future[Boolean] = tables.saveSolution(solution)

  // Read solution

  override def readSolutionFromPutRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SqlSolution] =
    request.body.asJson flatMap (_.asObj) flatMap (jsObj => jsObj.stringField(FORM_VALUE)) map (str => SqlSolution(user.username, collId, id, str))

  override def readSolutionFromPostRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SqlSolution] =
    SolutionFormHelper.stringSolForm.bindFromRequest() fold(_ => None, sol => Some(SqlSolution(user.username, collId, id, sol.learnerSolution)))

  // Views

  override def adminRenderEditRest(collOpt: Option[SqlCompleteScenario]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${SqlConsts.SHORTNAME_NAME}">Name der DB:</label>
       |    <input class="form-control" name="${SqlConsts.SHORTNAME_NAME}" id="${SqlConsts.SHORTNAME_NAME}" required ${collOpt map (coll => s"""value="${coll.coll.shortName}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  override def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlCompleteEx, numOfExes: Int): Future[Html] = {
    val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val futureOldOrDefSol: Future[String] = futureMaybeOldSolution(user.username, sqlScenario.id, exercise.ex.id) map (_ map (_.solution) getOrElse "")

    futureOldOrDefSol map (oldOrDefSol => views.html.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, numOfExes))
  }

  override def renderEditRest(exercise: SqlCompleteEx): Html = ???

  // Correction

  override protected def correctEx(user: User, learnerSolution: SqlSolution, sqlScenario: SqlScenario, exercise: SqlCompleteEx): Future[Try[SqlCorrResult]] =
    saveSolution(learnerSolution) map { _ =>
      (correctors.get(exercise.ex.exerciseType) zip daos.get(exercise.ex.exerciseType)).headOption match {
        case None                   => Failure(new Exception("There is no corrector or sql dao for " + exercise.ex.exerciseType))
        case Some((corrector, dao)) =>
          // FIXME: parse queries here!?!

          val sample = findBestFittingSample(learnerSolution.solution, exercise.samples.toList)
          Try(corrector.correct(dao, learnerSolution.solution, sample, exercise, sqlScenario))
      }
    }

  // Result handlers

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ??? // FIXME: implement...

  override def onSubmitCorrectionResult(user: User, result: SqlCorrResult): Html = result match {
    case res: SqlResult           => views.html.core.correction(result, sqlResult(res), user, this)
    case SqlParseFailed(_, error) =>
      //       FIXME: implement...
      Logger.error("There has been a sql correction error", error)
      Html("Es gab einen Fehler bei der Korrektur!")
  }

  override def onLiveCorrectionError(error: Throwable): JsValue = {
    Logger.error("There has been a correction error", error)
    Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!")
  }


  override def onLiveCorrectionResult(result: SqlCorrResult): JsValue = result match {
    case res: SqlResult           => res.toJson
    case SqlParseFailed(_, error) => Json.obj("msg" -> error.getMessage)
  }

}
