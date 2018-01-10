package controllers.exCollections

import javax.inject._

import controllers.Secured
import controllers.exCollections.SqlController._
import model.core.Levenshtein.levenshteinDistance
import model.core._
import model.sql.SqlConsts._
import model.sql.SqlEnums.SqlExerciseType
import model.sql._
import model.{CompleteCollectionWrapper, JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, duration}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object SqlController {

  val daos = Map(
    SqlExerciseType.SELECT -> SelectDAO,
    SqlExerciseType.CREATE -> CreateDAO,
    SqlExerciseType.UPDATE -> ChangeDAO,
    SqlExerciseType.INSERT -> ChangeDAO,
    SqlExerciseType.DELETE -> ChangeDAO
  )

  val correctors = Map(
    SqlExerciseType.CREATE -> CreateCorrector,
    SqlExerciseType.DELETE -> DeleteCorrector,
    SqlExerciseType.INSERT -> InsertCorrector,
    SqlExerciseType.SELECT -> SelectCorrector,
    SqlExerciseType.UPDATE -> UpdateCorrector
  )

  def findBestFittingSample(userSt: String, samples: List[SqlSample]): SqlSample =
    samples.minBy(samp => levenshteinDistance(samp.sample, userSt))

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

  override implicit val yamlFormat: YamlFormat[SqlCompleteScenario] = SqlYamlProtocol.SqlScenarioYamlFormat

  // db

  import profile.api._

  override protected def numOfExesInColl(id: Int): Future[Int] = tables.exercisesInScenario(id)

  override protected def saveRead(read: Seq[SqlCompleteScenario]): Future[Seq[Boolean]] = Future.sequence(read map { compScenario =>
    val scriptFilePath = toolObject.exerciseResourcesFolder / s"${compScenario.coll.shortName}.sql"

    daos.values.toList.distinct foreach (_.executeSetup(compScenario.coll.shortName, scriptFilePath))

    tables.saveSqlCompleteScenario(compScenario) map (_ => true)
  })

  override protected def wrap(compColl: SqlCompleteScenario): CompleteCollectionWrapper = new SqlScenarioWrapper(compColl)

  private def saveSolution(sol: SqlSolution) = db.run(tables.sqlSolutions insertOrUpdate sol)

  // Views for admin

  override def adminRenderEditRest(collOpt: Option[SqlCompleteScenario]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${SqlConsts.SHORTNAME_NAME}">Name der DB:</label>
       |    <input class="form-control" name="${SqlConsts.SHORTNAME_NAME}" id="${SqlConsts.SHORTNAME_NAME}" required ${collOpt map (coll => s"""value="${coll.coll.shortName}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  // User

  override protected def correctEx(user: User, learnerSolution: String, exercise: SqlCompleteEx, sqlScenario: SqlScenario): Try[SqlCorrResult] = Try({
    Await.result(saveSolution(SqlSolution(user.username, exercise.ex.collectionId, exercise.id, learnerSolution)), Duration(2, duration.SECONDS))

    val sample = findBestFittingSample(learnerSolution, exercise.samples toList)

    // FIXME: parse queries here!?!

    correctors(exercise.ex.exerciseType).correct(daos(exercise.ex.exerciseType), learnerSolution, sample, exercise, sqlScenario)
  })

  // Views for user

  override protected def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlCompleteEx, numOfExes: Int): Html = {
    val readTables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val oldOrDefSol: String = Await.result(
      db.run(tables.sqlSolutions.filter(sol => sol.username === user.username && sol.exerciseId === exercise.id && sol.scenarioId === exercise.ex.collectionId).result.headOption),
      Duration(2, duration.SECONDS)
    ) map (_.solution) getOrElse ""

    views.html.sql.sqlExercise(user, exercise, oldOrDefSol, readTables, sqlScenario, numOfExes)
  }

  // FIXME: get rif of cast...
  //  override def renderResult(correctionResult: SqlCorrResult): Html = correctionResult match {
  //    case res: SqlResult => views.html.sql.sqlResult(res)
  //    case res: SqlFailed => ???
  //  }

  protected def onSubmitCorrectionError(user: model.User, error: Throwable): play.api.mvc.Result = ??? // FIXME: implement...

  protected def onSubmitCorrectionResult(user: model.User, result: model.sql.SqlCorrResult): play.api.mvc.Result = ??? // FIXME: implement...

  protected def onLiveCorrectionError(error: Throwable): play.api.mvc.Result = ??? // FIXME: implement...

  protected def onLiveCorrectionResult(result: model.sql.SqlCorrResult): play.api.mvc.Result = ??? // FIXME: implement...

}
