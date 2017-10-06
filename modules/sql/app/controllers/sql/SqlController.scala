package controllers.sql

import java.sql.Connection

import scala.collection.JavaConverters.{ asScalaBufferConverter, seqAsJavaListConverter }
import scala.collection.mutable.ListBuffer
import scala.util.{ Failure, Success }

import controllers.core.{ BaseController, IdExController }
import javax.inject.Inject
import model.{ SqlSolution, SqlSolutionKey, SqlUser, StringConsts }
import model.Levenshtein
import model.CommonUtils.cleanly
import model.StringConsts.{ SELECT_ALL_DUMMY, SHOW_ALL_TABLES }
import model.exercise.{ SqlExercise, SqlExerciseType, SqlSample, SqlScenario }
import model.querycorrectors.QueryCorrector
import model.result.{ CompleteResult, EvaluationResult }
import model.sql.SqlQueryResult
import model.user.User
import play.data.{ DynamicForm, FormFactory }
import play.db.{ Database, NamedDatabase }
import play.mvc.Results

class SqlController @Inject() (f: FormFactory, @NamedDatabase("sqlselectroot") sqlSelect: Database, @NamedDatabase("sqlotherroot") sqlOther: Database)
  extends IdExController[SqlExercise, EvaluationResult](f, "sql", SqlExercise.finder, SqlToolObject) {

  def getUser = {
    val user = BaseController.getUser

    if (SqlUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new SqlUser(user.name).save()

    user
  }

  def filteredScenario(id: Int, exType: String, site: Int) = {
    val scenario = SqlScenario.finder.byId(id)

    if (scenario == null)
      Results.redirect(controllers.sql.routes.SqlController.index())

    val exerciseType = SqlExerciseType.valueOf(exType)

    if (site <= 0)
      Results.redirect(controllers.sql.routes.SqlController.filteredScenario(id, exType, 1))

    val start = SqlScenario.STEP * (site - 1)

    val allByType = scenario.getExercisesByType(exerciseType)
    val exercises = allByType.subList(start, Math.min(start + SqlScenario.STEP, allByType.size()))

    Results.ok(views.html.sqlScenario.render(getUser, exercises, scenario, SqlExerciseType.valueOf(exType), site))
  }

  def index = Results.ok(views.html.sqlIndex.render(getUser, SqlScenario.finder.all()))

  def scenarioes = Results.ok(views.html.scenarioes.render(getUser, SqlScenario.finder.all()))

  def getDBForExType(exerciseType: SqlExerciseType) = if (exerciseType == SqlExerciseType.SELECT) sqlSelect else sqlOther

  override def correctEx(form: DynamicForm, exercise: SqlExercise, user: User) = {
    val learnerSolution = form.get(StringConsts.FORM_VALUE)
    SqlController.saveSolution(user.name, learnerSolution, exercise.getId())

    val sample = SqlController.findBestFittingSample(learnerSolution, exercise.samples.asScala.toList)

    val corrector = QueryCorrector.getCorrector(exercise.exerciseType)
    val db = getDBForExType(exercise.exerciseType)

    val result = corrector.correct(db, learnerSolution, sample, exercise)

    Success(result)
  }

  override def renderExercise(user: User, exercise: SqlExercise) = {
    val tables = SqlController.readTablesInDatabase(sqlSelect, exercise.scenario.shortName)

    val oldSol = SqlSolution.finder.byId(new SqlSolutionKey(user.name, exercise.getId()))
    val oldOrDefSol = if (oldSol == null) "" else oldSol.sol

    views.html.sqlExercise.render(user, exercise, oldOrDefSol, tables.asJava)
  }

  //  override def renderExercises(user: User, exercises: List[SqlExercise]) = //FIXME: implement
  //    ???

  override def renderExesListRest = // FIXME: implement
    ???

  override def renderResult(correctionResult: CompleteResult[EvaluationResult]) = //FIXME: implement
    ???

}

object SqlController {
  def findBestFittingSample(userSt: String, samples: List[SqlSample]) = {
    samples.reduceLeft((samp1, samp2) ⇒
      if (Levenshtein.distance(samp1.sample, userSt) < Levenshtein.distance(samp2.sample, userSt))
        samp1 else samp2)
  }

  def readExistingTables(connection: Connection): List[String] = cleanly(connection.createStatement().executeQuery(SHOW_ALL_TABLES))(_.close)(existingTables ⇒ {
    var tableNames = new ListBuffer[String]()
    while (existingTables.next()) {
      tableNames += existingTables.getString(1)
    }
    tableNames.toList
  }) match {
    case Success(list) ⇒ list
    case Failure(_)    ⇒ List.empty
  }

  def readTableContent(connection: Connection, tableName: String): SqlQueryResult =
    cleanly(connection.prepareStatement(SELECT_ALL_DUMMY + tableName))(_.close)(result ⇒ new SqlQueryResult(result.executeQuery, tableName)) match {
      case Success(result) ⇒ result
      case Failure(_)      ⇒ null
    }

  def readTablesInDatabase(db: Database, databaseName: String): List[SqlQueryResult] = cleanly(db.getConnection)(_.close)(connection ⇒ {
    connection.setCatalog(databaseName)
    readExistingTables(connection).map(readTableContent(connection, _))
  }) match {
    case Success(queryResult) ⇒ queryResult
    case Failure(_)           ⇒ List.empty
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
