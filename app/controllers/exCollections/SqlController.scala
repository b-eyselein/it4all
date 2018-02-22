package controllers.exCollections

import controllers.Secured
import controllers.exCollections.SqlController._
import javax.inject._
import model.core._
import model.sql.SqlConsts._
import model.sql.SqlEnums.SqlExerciseType._
import model.sql._
import model.yaml.MyYamlFormat
import model.{CompleteCollectionWrapper, JsonFormat, User}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile
import views.html.sql._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

object SqlController {

  val daos = Map(
    SELECT -> SelectDAO, CREATE -> CreateDAO, UPDATE -> ChangeDAO, INSERT -> ChangeDAO, DELETE -> ChangeDAO
  )

  val correctors = Map(
    CREATE -> CreateCorrector, DELETE -> DeleteCorrector, INSERT -> InsertCorrector, SELECT -> SelectCorrector, UPDATE -> UpdateCorrector
  )

  def findBestFittingSample(userSt: String, samples: List[SqlSample]): SqlSample = samples.minBy(samp => Levenshtein.levenshteinDistance(samp.sample, userSt))

}

@Singleton
class SqlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: SqlTableDefs)(implicit ec: ExecutionContext)
  extends AExCollectionController[SqlExercise, SqlCompleteEx, SqlScenario, SqlCompleteScenario, EvaluationResult, SqlCorrResult, SqlTableDefs](cc, dbcp, t, SqlToolObject)
    with HasDatabaseConfigProvider[JdbcProfile] with JsonFormat with Secured {

  override type SolType = String

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] =
    request.body.asJson flatMap (_.asObj flatMap (jsObj => jsObj.stringField(FORM_VALUE)))

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest() fold(_ => None, sol => Some(sol.learnerSolution))

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[SqlCompleteScenario] = SqlYamlProtocol.SqlScenarioYamlFormat

  // db

  override protected def numOfExesInColl(id: Int): Future[Int] = tables.exercisesInScenario(id)

  override protected def saveRead(read: Seq[SqlCompleteScenario]): Future[Seq[Boolean]] = Future.sequence(read map { compScenario =>
    val scriptFilePath = toolObject.exerciseResourcesFolder / s"${compScenario.coll.shortName}.sql"

    daos.values.toList.distinct foreach (_.executeSetup(compScenario.coll.shortName, scriptFilePath))

    tables.saveSqlCompleteScenario(compScenario) map (_ => true)
  })

  override protected def wrap(compColl: SqlCompleteScenario): CompleteCollectionWrapper = new SqlScenarioWrapper(compColl)

  private def saveSolution(username: String, scenarioId: Int, exerciseId: Int, solution: String) =
    tables.saveSolution(SqlSolution(username, scenarioId, exerciseId, solution))

  // Views for admin

  override def adminRenderEditRest(collOpt: Option[SqlCompleteScenario]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${SqlConsts.SHORTNAME_NAME}">Name der DB:</label>
       |    <input class="form-control" name="${SqlConsts.SHORTNAME_NAME}" id="${SqlConsts.SHORTNAME_NAME}" required ${collOpt map (coll => s"""value="${coll.coll.shortName}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  // User

  override protected def correctEx(user: User, learnerSolution: String, exercise: SqlCompleteEx, sqlScenario: SqlScenario): Future[Try[SqlCorrResult]] =
    saveSolution(user.username, exercise.ex.collectionId, exercise.id, learnerSolution) map { _ =>
      ((correctors get exercise.ex.exerciseType) zip (daos get exercise.ex.exerciseType)).headOption match {
        case None                   => Failure(new Exception("There is no corrector or sql dao for " + exercise.ex.exerciseType))
        case Some((corrector, dao)) =>
          // FIXME: parse queries here!?!

          val sample = findBestFittingSample(learnerSolution, exercise.samples.toList)
          Try(corrector.correct(dao, learnerSolution, sample, exercise, sqlScenario))
      }
    }


  // Views for user

  override protected def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlCompleteEx, numOfExes: Int): Future[Html] = {
    val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val futureOldOrDefSol: Future[String] = tables.oldSolution(user, sqlScenario.id, exercise.id) map (_ map (_.solution) getOrElse "")

    futureOldOrDefSol map (oldOrDefSol => views.html.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, numOfExes))
  }

  protected def onSubmitCorrectionError(user: User, error: Throwable): Result = ??? // FIXME: implement...

  protected def onSubmitCorrectionResult(user: User, result: SqlCorrResult): Result = result match {
    case res: SqlResult           => Ok(views.html.core.correction(result, sqlResult(res), user, toolObject))
    case SqlParseFailed(_, error) =>
      // FIXME: implement...
      Logger.error("There has been a sql correction error", error)
      BadRequest("Es gab einen Fehler bei der Korrektur!")
  }

  protected def onLiveCorrectionError(error: Throwable): Result = {
    Logger.error("There has been a correction error", error)
    // FIXME: implement...
    BadRequest("TODO!")
  }

  protected def onLiveCorrectionResult(result: SqlCorrResult): Result = result match {
    case res: SqlResult           => Ok(res.toJson)
    case SqlParseFailed(_, error) => BadRequest(Json.obj("msg" -> error.getMessage))
  }

}
