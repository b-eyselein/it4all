package controllers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.Util;
import model.correctionresult.SqlCorrectionResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;
import play.mvc.Http.Request;

public class SQL extends ExerciseController {

  private static final String SHOW_ALL_TABLES = "SHOW TABLES";

  private static final String SELECT_ALL = "SELECT * FROM ";

  private Database sqlSelect;

  private Database sqlOther;

  @Inject
  public SQL(Util theUtil, FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theUtil, theFactory);
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  public Result commit(int id) {
    User user = UserManagement.getCurrentUser();
    CompleteResult result = correct(request(), user, id);

    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), id, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), id, result));
      return ok(views.html.correction.render("SQL", new Html("TODO!"), result.getLearnerSolution(), user));
    }
  }

  public Result exercise(int id) {
    User user = UserManagement.getCurrentUser();

    SqlExercise exercise = SqlExercise.finder.byId(id);

    if(exercise == null)
      return redirect(controllers.sql.routes.SQL.index());

    List<SqlQueryResult> tables = readTablesInDatabase(exercise.scenario.shortName);

    log(user, new ExerciseStartEvent(request(), id));

    return ok(views.html.sqlexercise.render(user, exercise, tables));
  }

  public Result index() {
    return ok(views.html.sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
  }

  public Result scenario(String scenarioName, String exType, int start) {
    SqlScenario scenario = SqlScenario.finder.byId(scenarioName);
    if(scenario == null)
      return redirect(controllers.sql.routes.SQL.index());

    return ok(views.html.sqlscenario.render(UserManagement.getCurrentUser(), scenario, SqlExerciseType.valueOf(exType),
        start));
  }

  private Database getDatabaseForExerciseType(SqlExerciseType exerciseType) {
    if(exerciseType == SqlExerciseType.SELECT)
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

  protected CompleteResult correct(Request request, User user, int id) {
    SqlExercise exercise = SqlExercise.finder.byId(id);

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    FeedbackLevel feedbackLevel = FeedbackLevel.valueOf(form.get("feedbackLevel"));

    // FIXME: Speichern der Lösung?!?

    Database database = getDatabaseForExerciseType(exercise.exercisetype);

    if(learnerSolution.isEmpty())
      return new SqlCorrectionResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Sie haben eine leere Query abgegeben!")));

    return SqlCorrector.correct(database, learnerSolution, exercise, feedbackLevel);
  }

}
