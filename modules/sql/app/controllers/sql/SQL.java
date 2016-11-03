package controllers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Secured;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.Util;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.correction;
import views.html.sqlexercise;
import views.html.sqloverview;

@Authenticated(Secured.class)
public class SQL extends ExerciseController {

  private static final String SHOW_ALL_TABLES = "SHOW TABLES";

  private static final String SELECT_ALL = "SELECT * FROM ";

  private Database sqlSelect;

  private Database sqlOther;

  @SuppressWarnings("unused")
  private SqlStartUpChecker checker;

  @Inject
  public SQL(Util theUtil, FormFactory theFactory, SqlStartUpChecker theChecker,
      @NamedDatabase("sqlselectuser") Database theSqlSelect, @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theUtil, theFactory);
    checker = theChecker;
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  public Result commit(String scenarioName, String exerciseType, int exerciseId) {
    SqlExerciseType type = SqlExerciseType.valueOf(exerciseType);
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId, type));

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    FeedbackLevel feedbackLevel = FeedbackLevel.valueOf(form.get("feedbackLevel"));
    
    // FIXME: Speichern der Lösung?!?

    Database database = getDatabaseForExerciseType(exerciseType);

    List<EvaluationResult> result = new LinkedList<>();
    if(learnerSolution.isEmpty())
      result.add(new EvaluationFailed("Sie haben eine leere Query abgegeben!"));
    else if(exercise == null)
      result.add(new EvaluationFailed("Diese Aufgabe existiert nicht!"));
    else
      result = SqlCorrector.correct(database, learnerSolution, exercise, feedbackLevel);

    if(wantsJsonResponse())
      return ok(Json.toJson(result));
    else
      return ok(correction.render("SQL", learnerSolution, result, UserManagement.getCurrentUser()));
  }

  public Result exercise(String scenarioName, String exerciseType, int exerciseId) {
    User user = UserManagement.getCurrentUser();

    SqlExerciseType type = SqlExerciseType.valueOf(exerciseType);

    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId, type));

    if(exercise == null)
      return badRequest("There is no such exercise!");

    List<SqlQueryResult> tables = readTablesInDatabase(scenarioName);

    return ok(sqlexercise.render(user, exercise, tables));

  }

  public Result index() {
    return ok(sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
  }

  private Database getDatabaseForExerciseType(String exerciseType) {
    if("SELECT".equals(exerciseType))
      return sqlSelect;
    return sqlOther;
  }

  private List<String> readExistingTables(Connection connection) {
    List<String> tableNames = new LinkedList<>();
    try(Statement statement = connection.createStatement();
        ResultSet existingTables = statement.executeQuery(SHOW_ALL_TABLES)) {
      while(existingTables.next())
        tableNames.add(existingTables.getString(1));
    } catch (SQLException e) {
      Logger.error("There was an error reading the table names from a database", e);
    }
    return tableNames;
  }

  private SqlQueryResult readTableContent(Connection connection, String tableName) {
    try(PreparedStatement selectStatement = connection.prepareStatement(SELECT_ALL + tableName);) {
      ResultSet tableResult = selectStatement.executeQuery();
      return new SqlQueryResult(tableResult, tableName);
    } catch (SQLException e) {
      Logger.error("There has been an error reading the data from the table " + tableName, e);
      return null;
    }
  }

  private List<SqlQueryResult> readTablesInDatabase(String databaseName) {
    List<SqlQueryResult> tables = new LinkedList<>();

    try(Connection connection = sqlSelect.getConnection()) {
      connection.setCatalog(databaseName);

      List<String> tableNames = readExistingTables(connection);

      for(String tableName: tableNames) {
        SqlQueryResult tableResult = readTableContent(connection, tableName);
        tables.add(tableResult);
      }

    } catch (SQLException e) {
      Logger.error("Es gab einen Fehler beim Auslesen der Tabellen!", e);
    }

    return tables;
  }

}
