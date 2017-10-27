package controllers.sql

import java.sql.Connection
import javax.inject._

import controllers.core.excontrollers.IdExController
import model.User
import model.core.CommonUtils.cleanly
import model.core.StringConsts.{SELECT_ALL_DUMMY, SHOW_ALL_TABLES}
import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.sql.SqlEnums.SqlExerciseType
import model.sql.{SqlQueryResult, _}
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.db.Database
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class SqlController @Inject()(cc: ControllerComponents /*, @NamedDatabase("sqlselectroot") sqlSelect: Database, @NamedDatabase("sqlotherroot") sqlOther: Database*/ ,
                              dbcp: DatabaseConfigProvider, r: Repository)
                             (implicit ec: ExecutionContext)
  extends IdExController[SqlExercise, EvaluationResult](cc, dbcp, r, SqlToolObject) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  //  override def getUser(implicit request: Request[AnyContent]): User = {
  //    val user = super.getUser
  //
  //    // Make sure there is a corresponding entrance in other db...
  //    Option(SqlUser.finder.byId(user.name)) match {
  //      case None => new SqlUser(user.name).save()
  //      case Some(_) => Unit
  //    }
  //
  //    user
  //  }

  def filteredScenario(id: Int, exType: String, site: Int): EssentialAction = withUser { user =>
    implicit request =>
      //      Option(SqlScenario.finder.byId(id)) match {
      //        case None => Redirect(controllers.sql.routes.SqlController.index())
      //        case Some(scenario) =>
      //          if (site <= 0)
      //            Redirect(controllers.sql.routes.SqlController.filteredScenario(id, exType))
      //          else {
      //            val start = SqlScenario.STEP * (site - 1)
      //
      //            val allByType = scenario.getExercisesByType(SqlExerciseType.valueOf(exType))
      //            val exercises = allByType.subList(start, Math.min(start + SqlScenario.STEP, allByType.size))
      //
      //            Ok(views.html.sqlScenario.render(user, exercises.asScala.toList, scenario, SqlExerciseType.valueOf(exType), site))
      //          }
      //      }
      null
  }

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.sql.sqlIndex.render(user, List.empty /* SqlScenario.finder.all.asScala.toList*/)) }

  def scenarioes: EssentialAction = withUser { user => implicit request => Ok(views.html.sql.scenarioes.render(user, List.empty /* SqlScenario.finder.all.asScala.toList*/)) }

  def getDBForExType(exerciseType: SqlExerciseType): Database = if (exerciseType == SqlExerciseType.SELECT) null /* sqlSelect else sqlOther*/ else null

  override def correctEx(sol: StringSolution, exercise: Option[SqlExercise], user: User): Try[SqlResult] = Try({
    //    val learnerSolution = sol.learnerSolution
    //    saveSolution(user.name, learnerSolution, exercise.id)
    //
    //    val sample = findBestFittingSample(learnerSolution, exercise.samples.toList)
    //
    //    val corrector = QueryCorrector.getCorrector(exercise.exerciseType)
    //    val db = getDBForExType(exercise.exerciseType)
    //
    //    corrector.correct(db, learnerSolution, sample, exercise)
    null
  })

  override def renderExercise(user: User, exercise: SqlExercise): Html = {
    //    val tables = readTablesInDatabase(sqlSelect, exercise.scenario.shortName)
    //
    //    val oldOrDefSol = Option(SqlSolution.finder.byId(new SqlSolutionKey(user.name, exercise.id))) match {
    //      case None => ""
    //      case Some(oldSol) => oldSol.sol
    //    }
    //
    //    views.html.sqlExercise.render(user, exercise, oldOrDefSol, tables)
    new Html("")
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
    case Failure(_)    => List.empty
  }

  def readTableContent(connection: Connection, tableName: String): SqlQueryResult =
    cleanly(connection.prepareStatement(SELECT_ALL_DUMMY + tableName))(_.close)(result => new SqlQueryResult(result.executeQuery, tableName)) match {
      case Success(result) => result
      case Failure(_)      => null
    }

  def readTablesInDatabase(db: Database, databaseName: String): List[SqlQueryResult] = cleanly(db.getConnection)(_.close)(connection => {
    connection.setCatalog(databaseName)
    readExistingTables(connection).map(readTableContent(connection, _))
  }) match {
    case Success(queryResult) => queryResult
    case Failure(_)           => List.empty
  }

  def saveSolution(userName: String, learnerSolution: String, id: Int) {
    //    val key = new SqlSolutionKey(userName, id)
    //    var solution = Option(SqlSolution.finder.byId(key)).getOrElse(new SqlSolution(key))
    //
    //    solution.sol = learnerSolution
    //    solution.save()
  }
}
