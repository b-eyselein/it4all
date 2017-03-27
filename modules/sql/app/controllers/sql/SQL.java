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
import model.EmptySolutionException;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.Util;
import model.correctionresult.SqlResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.EvaluationFailed;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class SQL extends ExerciseController {

  private static final String SHOW_ALL_TABLES = "SHOW TABLES";

  private static final String SELECT_ALL = "SELECT * FROM ";

  private static final String FEEDBACK_LEVEL_FORM_VALUE = "feedback";
  
  private Database sqlSelect;

  private Database sqlOther;

  @Inject
  public SQL(Util theUtil, FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theUtil, theFactory);
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  public Result correct(int id) {
    final User user = UserManagement.getCurrentUser();
    final SqlExercise exercise = SqlExercise.finder.byId(id);

    DynamicForm form = factory.form().bindFromRequest();
    final String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    final String fbLevelAsString = form.get(FEEDBACK_LEVEL_FORM_VALUE);

    FeedbackLevel feedbackLevel = FeedbackLevel.MINIMAL_FEEDBACK;
    if(fbLevelAsString != null && !fbLevelAsString.isEmpty())
      feedbackLevel = FeedbackLevel.valueOf(fbLevelAsString);

    SqlResult result;
    try {
      result = correct(learnerSolution, exercise, feedbackLevel);
    } catch (EmptySolutionException e) { // NOSONAR
      return ok(views.html.error.render(user,
          "<div class=\"alert alert-danger\">Sie haben eine leere L&ouml;sung abgegeben!</div>"));
    }

    log(user, new ExerciseCompletionEvent(request(), id, result.getResults()));
    return ok(views.html.correction.render("SQL", views.html.sqlresult.render(result), learnerSolution, user));
  }

  public Result correctLive(int id) {
    final User user = UserManagement.getCurrentUser();
    final SqlExercise exercise = SqlExercise.finder.byId(id);

    DynamicForm form = factory.form().bindFromRequest();
    final String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    final String fbLevelAsString = form.get(FEEDBACK_LEVEL_FORM_VALUE);

    FeedbackLevel feedbackLevel = FeedbackLevel.MINIMAL_FEEDBACK;
    if(fbLevelAsString != null && !fbLevelAsString.isEmpty())
      feedbackLevel = FeedbackLevel.valueOf(fbLevelAsString);

    SqlResult result;
    try {
      result = correct(learnerSolution, exercise, feedbackLevel);
    } catch (EmptySolutionException e) { // NOSONAR
      return ok(views.html.correctionerror.render("Sie haben eine leere L&ouml;sung abgegeben!"));
    }

    log(user, new ExerciseCorrectionEvent(request(), id, result.getResults()));
    return ok(views.html.sqlresult.render(result));
  }

  public Result exercise(int id) {
    final User user = UserManagement.getCurrentUser();

    SqlExercise exercise = SqlExercise.finder.byId(id);

    if(exercise == null)
      return redirect(controllers.sql.routes.SQL.index());

    List<SqlQueryResult> tables = readTablesInDatabase(exercise.scenario.shortName);

    log(user, new ExerciseStartEvent(request(), id));

    return ok(views.html.sql.render(user, exercise, tables));
  }

  public Result index() {
    return ok(views.html.sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
  }

  public Result scenario(String scenarioName, String exType, int start) {
    final SqlScenario scenario = SqlScenario.finder.byId(scenarioName);

    if(scenario == null)
      return redirect(controllers.sql.routes.SQL.index());

    return ok(views.html.sqlscenario.render(UserManagement.getCurrentUser(), scenario, SqlExerciseType.valueOf(exType),
        start));
  }

  private Database getDatabaseForExerciseType(SqlExerciseType exerciseType) {
    return exerciseType == SqlExerciseType.SELECT ? sqlSelect : sqlOther;
  }

  private List<String> readExistingTables(Connection connection) {
    final List<String> tableNames = new LinkedList<>();
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
      final ResultSet tableResult = selectStatement.executeQuery();
      return new SqlQueryResult(tableResult, tableName);
    } catch (SQLException e) {
      Logger.error("There has been an error reading the data from the table " + tableName, e);
      return null;
    }
  }

  private List<SqlQueryResult> readTablesInDatabase(String databaseName) {
    final List<SqlQueryResult> tables = new LinkedList<>();

    try(Connection connection = sqlSelect.getConnection()) {
      connection.setCatalog(databaseName);

      List<String> tableNames = readExistingTables(connection);

      for(String tableName: tableNames) {
        final SqlQueryResult tableResult = readTableContent(connection, tableName);
        tables.add(tableResult);
      }

    } catch (SQLException e) {
      Logger.error("Es gab einen Fehler beim Auslesen der Tabellen!", e);
    }

    return tables;
  }

  protected SqlResult correct(String learnerSolution, SqlExercise exercise, FeedbackLevel feedbackLevel)
      throws EmptySolutionException {
    // FIXME: Speichern der Lösung?!?

    Database database = getDatabaseForExerciseType(exercise.exercisetype);

    // FIXME: empty solution!
    if(learnerSolution.isEmpty())
      throw new EmptySolutionException("Sie haben eine leere Query abgegeben!");

    return SqlCorrector.correct(database, learnerSolution, exercise, feedbackLevel);
  }

}
