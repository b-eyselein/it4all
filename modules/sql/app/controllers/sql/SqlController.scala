package controllers.sql

import java.sql.Connection

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.collection.mutable.ListBuffer
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import controllers.core.ScalaExerciseController
import javax.inject.Inject
import model.Levenshtein
import model.ScalaUtils.cleanly
import model.SqlSolution
import model.SqlSolutionKey
import model.SqlUser
import model.StringConsts._
import model.exercise.SqlExercise
import model.exercise.SqlExerciseType
import model.exercise.SqlSample
import model.exercise.SqlScenario
import model.querycorrectors.QueryCorrector
import model.querycorrectors.SqlResult
import model.sql.SqlQueryResult
import model.user.User
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.db.Database
import play.db.NamedDatabase

class SqlController @Inject() (cc: ControllerComponents, @NamedDatabase("sqlselectroot") sqlSelect: Database, @NamedDatabase("sqlotherroot") sqlOther: Database)
  extends ScalaExerciseController[SqlExercise, SqlResult](cc, "sql", SqlExercise.finder) {

  override def getUser(request: Request[AnyContent]): User = {
    val user = super.getUser(request)

    if (SqlUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new SqlUser(user.name).save()

    user
  }

  def filteredScenario(id: Int, exType: String, site: Int) = Action { request =>
    val scenario = SqlScenario.finder.byId(id)

    if (scenario == null)
      Redirect(controllers.sql.routes.SqlController.index())

    val exerciseType = SqlExerciseType.valueOf(exType)

    if (site <= 0)
      Redirect(controllers.sql.routes.SqlController.filteredScenario(id, exType, 1))

    val start = SqlScenario.STEP * (site - 1)

    val allByType = scenario.getExercisesByType(exerciseType)
    val exercises = allByType.subList(start, Math.min(start + SqlScenario.STEP, allByType.size()))

    Ok(views.html.sqlScenario.render(getUser(request), exercises, scenario, SqlExerciseType.valueOf(exType), site))
  }

  def index = Action { request => Ok(views.html.sqlIndex.render(getUser(request), SqlScenario.finder.all())) }

  def scenarioes = Action { request => Ok(views.html.scenarioes.render(getUser(request), SqlScenario.finder.all())) }

  def getDBForExType(exerciseType: SqlExerciseType) = if (exerciseType == SqlExerciseType.SELECT) sqlSelect else sqlOther

  override def correctExercise(learnerSolution: String, exercise: SqlExercise, user: User): Try[List[SqlResult]] = {
    SqlController.saveSolution(user.name, learnerSolution, exercise.getId())

    val sample = SqlController.findBestFittingSample(learnerSolution, exercise.samples.asScala.toList)

    val corrector = QueryCorrector.getCorrector(exercise.exerciseType)
    val db = getDBForExType(exercise.exerciseType)

    val result = corrector.correct(db, learnerSolution, sample, exercise)

    Success(List(result))
  }

  override def renderExercise(user: User, exercise: SqlExercise) = {
    val tables = SqlController.readTablesInDatabase(sqlSelect, exercise.scenario.getShortName())

    val oldSol = SqlSolution.finder.byId(new SqlSolutionKey(user.name, exercise.getId()))
    val oldOrDefSol = if (oldSol == null) "" else oldSol.sol

    views.html.sqlExercise.render(user, exercise, oldOrDefSol, tables.asJava)
  }

  override def renderExercises(user: User, exercises: List[SqlExercise]) = {
    // TODO Auto-generated method stub
    null
  }

  override def renderResult(correctionResult: List[SqlResult]) = views.html.resultTemplates.sqlResult.render(correctionResult(0))

}

object SqlController {
  def findBestFittingSample(userSt: String, samples: List[SqlSample]) = {
    samples.reduceLeft((samp1, samp2) =>
      if (Levenshtein.distance(samp1.sample, userSt) < Levenshtein.distance(samp2.sample, userSt))
        samp1 else samp2)
  }

  def readExistingTables(connection: Connection): List[String] = cleanly(connection.createStatement().executeQuery(SHOW_ALL_TABLES))(_.close)(existingTables => {
    var tableNames = new ListBuffer[String]()
    while (existingTables.next()) {
      tableNames += existingTables.getString(1)
    }
    tableNames.toList
  }) match {
    case Success(list) => list
    case Failure(_) => List.empty
  }

  def readTableContent(connection: Connection, tableName: String): SqlQueryResult =
    cleanly(connection.prepareStatement(SELECT_ALL_DUMMY + tableName))(_.close)(result => new SqlQueryResult(result.executeQuery, tableName)) match {
      case Success(result) => result
      case Failure(_) => null
    }

  def readTablesInDatabase(db: Database, databaseName: String): List[SqlQueryResult] = cleanly(db.getConnection)(_.close)(connection => {
    connection.setCatalog(databaseName)
    readExistingTables(connection).map(readTableContent(connection, _))
  }) match {
    case Success(queryResult) => queryResult
    case Failure(_) => List.empty
  }

  def saveSolution(userName: String, learnerSolution: String, id: Int) {
    val key = new SqlSolutionKey(userName, id)

    var solution = SqlSolution.finder.byId(key)
    if (solution == null)
      solution = new SqlSolution(key)

    solution.sol = learnerSolution
    solution.save()
  }
}
