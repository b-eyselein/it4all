package controllers.exCollections

import javax.inject._

import controllers.Secured
import controllers.exCollections.SqlController._
import model.core.Levenshtein.levenshteinDistance
import model.core._
import model.sql.SqlConsts._
import model.sql.SqlEnums.SqlExerciseType
import model.sql.SqlYamlProtocol.SqlScenarioYamlFormat
import model.sql._
import model.{JsonFormat, User}
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
    samples.reduceLeft((samp1, samp2) => if (levenshteinDistance(samp1.sample, userSt) < levenshteinDistance(samp2.sample, userSt)) samp1 else samp2)

}

@Singleton
class SqlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AExCollectionController[SqlExercise, SqlScenario, EvaluationResult](cc, dbcp, r, SqlToolObject)
    with HasDatabaseConfigProvider[JdbcProfile] with JsonFormat with Secured {

  override type SolType = String

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] =
    request.body.asJson flatMap (_.asObj flatMap (jsObj => jsObj.stringField(FORM_VALUE)))

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest() fold(_ => None, sol => Some(sol.learnerSolution))


  // Yaml

  override type CompColl = SqlCompleteScenario

  override type CompEx = SqlCompleteEx

  override implicit val yamlFormat: YamlFormat[SqlCompleteScenario] = SqlScenarioYamlFormat

  // db

  import profile.api._

  override type TQ = repo.SqlScenarioesTable

  //noinspection TypeAnnotation
  override def tq = repo.sqlScenarioes

  override protected def completeColls: Future[Seq[SqlCompleteScenario]] = repo.completeSqlScenarioes

  override protected def completeCollById(id: Int): Future[Option[SqlCompleteScenario]] = repo.completeScenarioById(id)

  override protected def futureCompleteExById(collId: Int, id: Int): Future[Option[SqlCompleteEx]] = repo.sqlExercises.completeEx(collId, id)

  override protected def saveRead(read: Seq[SqlCompleteScenario]): Future[Seq[Boolean]] = Future.sequence(read map { compScenario =>
    val scriptFilePath = toolObject.exerciseResourcesFolder / s"${compScenario.coll.shortName}.sql"

    daos.values foreach (_.executeSetup(compScenario.coll.shortName, scriptFilePath))

    repo.saveSqlCompleteScenario(compScenario) map (_ => true)
  })

  private def saveSolution(sol: SqlSolution) = db.run(repo.sqlSolutions insertOrUpdate sol)

  //  override def newExerciseForm: EssentialAction = withAdmin { user => implicit request => Ok(views.html.sql.newExerciseForm.render(user, null)) }

  def scenarioAdmin(id: Int): EssentialAction = withAdmin { user => implicit request => Ok(views.html.sql.scenarioAdmin.render(user, null /* SqlScenario.finder.byId(id)*/)) }

  override def renderCollectionCreated(collections: List[model.core.SingleReadingResult[SqlScenario]]): play.twirl.api.Html = ???

  override def renderExCollCreationForm(user: model.User, collection: SqlScenario): play.twirl.api.Html = ??? //    views.html.sql.newScenarioForm.render(user, scenario)

  override def renderExEditForm(user: model.User, exercise: SqlScenario, isCreation: Boolean): play.twirl.api.Html = ???

  override def renderExerciseCollections(user: model.User, allCollections: List[SqlScenario]): play.twirl.api.Html = ???

  // FIXME: stubs end...

  // User

  override protected def correctEx(learnerSolution: String, exercise: SqlCompleteEx, sqlScenario: SqlScenario, user: User): Try[SqlCorrResult] = Try({
    Await.result(saveSolution(SqlSolution(user.username, exercise.ex.scenarioId, exercise.id, learnerSolution)), Duration(2, duration.SECONDS))

    val sample = findBestFittingSample(learnerSolution, exercise.samples toList)

    correctors(exercise.ex.exerciseType).correct(daos(exercise.ex.exerciseType), learnerSolution, sample, exercise, sqlScenario)
  })

  override protected def renderExercise(user: User, sqlScenario: SqlScenario, exercise: SqlCompleteEx): Html = {
    val tables: Seq[SqlQueryResult] = SelectDAO.tableContents(sqlScenario.shortName)

    val oldOrDefSol: String = Await.result(
      db.run(repo.sqlSolutions.filter(sol => sol.username === user.username && sol.exerciseId === exercise.id && sol.scenarioId === exercise.ex.scenarioId).result.headOption),
      Duration(2, duration.SECONDS)
    ) map (_.solution) getOrElse ""

    views.html.sql.sqlExercise(user, exercise, oldOrDefSol, tables, sqlScenario.shortName + ".png")
  }

  // FIXME: get rif of cast...
  override def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = correctionResult match {
    case res: SqlResult => views.html.sql.sqlResult(res)
    case _              => new Html("")
  }

}
