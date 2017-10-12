package controllers.sql

import java.sql.Connection
import javax.inject.Inject

import controllers.core.IdExController
import controllers.sql.SqlController._
import model.CommonUtils.cleanly
import model.StringConsts.{SELECT_ALL_DUMMY, SHOW_ALL_TABLES}
import model._
import model.exercise.{SqlExercise, SqlExerciseType, SqlSample, SqlScenario}
import model.querycorrectors.{QueryCorrector, SqlResult}
import model.result.{CompleteResult, EvaluationResult}
import model.sql.SqlQueryResult
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.db.{Database, NamedDatabase}
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.collection.JavaConverters.{asScalaBufferConverter, seqAsJavaListConverter}
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class SqlController @Inject()(f: FormFactory, @NamedDatabase("sqlselectroot") sqlSelect: Database, @NamedDatabase("sqlotherroot") sqlOther: Database)
  extends IdExController[SqlExercise, EvaluationResult](f, SqlExercise.finder, SqlToolObject) {

  override def getUser: User = {
    val user = super.getUser

    // Make sure there is a corresponding entrance in other db...
    Option(SqlUser.finder.byId(user.name)) match {
      case None => new SqlUser(user.name).save()
      case Some(_) => Unit
    }

    user
  }

  def filteredScenario(id: Int, exType: String, site: Int): Result = Option(SqlScenario.finder.byId(id)) match {
    case None => Results.redirect(controllers.sql.routes.SqlController.index())
    case Some(scenario) =>
      if (site <= 0)
        Results.redirect(controllers.sql.routes.SqlController.filteredScenario(id, exType))
      else {
        val start = SqlScenario.STEP * (site - 1)

        val allByType = scenario.getExercisesByType(SqlExerciseType.valueOf(exType))
        val exercises = allByType.subList(start, Math.min(start + SqlScenario.STEP, allByType.size))

        Results.ok(views.html.sqlScenario.render(getUser, exercises, scenario, SqlExerciseType.valueOf(exType), site))
      }
  }

  def index: Result = Results.ok(views.html.sqlIndex.render(getUser, SqlScenario.finder.all))

  def scenarioes: Result = Results.ok(views.html.scenarioes.render(getUser, SqlScenario.finder.all))

  def getDBForExType(exerciseType: SqlExerciseType): Database = if (exerciseType == SqlExerciseType.SELECT) sqlSelect else sqlOther

  override def correctEx(form: DynamicForm, exercise: SqlExercise, user: User): Try[SqlResult] = Try({
    val learnerSolution = form.get(StringConsts.FORM_VALUE)
    saveSolution(user.name, learnerSolution, exercise.id)

    val sample = findBestFittingSample(learnerSolution, exercise.samples.asScala.toList)

    val corrector = QueryCorrector.getCorrector(exercise.exerciseType)
    val db = getDBForExType(exercise.exerciseType)

    corrector.correct(db, learnerSolution, sample, exercise)
  })

  override def renderExercise(user: User, exercise: SqlExercise): Html = {
    val tables = readTablesInDatabase(sqlSelect, exercise.scenario.shortName)

    val oldOrDefSol = Option(SqlSolution.finder.byId(new SqlSolutionKey(user.name, exercise.id))) match {
      case None => ""
      case Some(oldSol) => oldSol.sol
    }

    views.html.sqlExercise.render(user, exercise, oldOrDefSol, tables.asJava)
  }

  override def renderExesListRest: Html = ??? // FIXME: implement


  override def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? //FIXME: implement

}

object SqlController {
  def findBestFittingSample(userSt: String, samples: List[SqlSample]): SqlSample = {
    samples.reduceLeft((samp1, samp2) =>
      if (Levenshtein.distance(samp1.sample, userSt) < Levenshtein.distance(samp2.sample, userSt))
        samp1 else samp2)
  }

  def readExistingTables(connection: Connection): List[String] = cleanly(connection.createStatement.executeQuery(SHOW_ALL_TABLES))(_.close)(existingTables => {
    var tableNames = ListBuffer.empty[String]
    while (existingTables.next) {
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
    var solution = Option(SqlSolution.finder.byId(key)).getOrElse(new SqlSolution(key))

    solution.sol = learnerSolution
    solution.save()
  }
}
