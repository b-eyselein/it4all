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
import model.EmptySolutionException;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.StringConsts;
import model.correctionresult.SqlResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;

public class Sql extends ExerciseController {

  private static final String SHOW_ALL_TABLES = "SHOW TABLES";

  private static final String SELECT_ALL = "SELECT * FROM ";

  private static final String FEEDBACK_LEVEL_FORM_VALUE = "feedback";

  private Database sqlSelect;

  private Database sqlOther;

  @Inject
  public Sql(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, "sql");
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  public Result correct(int scenarioId, int exerciseId) {
    final User user = getUser();
    final SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioId, exerciseId));

    DynamicForm form = factory.form().bindFromRequest();
    final String learnerSolution = form.get(StringConsts.FORM_VALUE);
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

    log(user, new ExerciseCompletionEvent(request(), exerciseId, result.getResults()));
    return ok(views.html.correction.render("SQL", views.html.sqlresult.render(result), learnerSolution, user));
  }

  public Result correctLive(int scenarioId, int exerciseId) {
    final User user = getUser();
    final SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioId, exerciseId));

    DynamicForm form = factory.form().bindFromRequest();
    final String learnerSolution = form.get(StringConsts.FORM_VALUE);
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

    log(user, new ExerciseCorrectionEvent(request(), exerciseId, result.getResults()));
    return ok(views.html.sqlresult.render(result));
  }

  public Result exercise(int scenarioId, int exerciseId) {
    final User user = getUser();

    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioId, exerciseId));

    if(exercise == null)
      return redirect(controllers.sql.routes.Sql.index());

    List<SqlQueryResult> tables = readTablesInDatabase(exercise.scenario.shortName);

    log(user, new ExerciseStartEvent(request(), exerciseId));

    return ok(views.html.sqlExercise.render(user, exercise, tables));
  }

  public Result filteredScenario(int id, String exType, int start) {
    final SqlScenario scenario = SqlScenario.finder.byId(id);

    if(scenario == null)
      return redirect(controllers.sql.routes.Sql.index());

    return ok(views.html.sqlscenario.render(getUser(), scenario, SqlExerciseType.valueOf(exType), start));
  }

  public Result index() {
    return ok(views.html.sqlIndex.render(getUser(), SqlScenario.finder.all()));
  }

  public Result scenarioes() {
    return ok(views.html.scenarioes.render(getUser(), SqlScenario.finder.all()));
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

    Database database = getDatabaseForExerciseType(exercise.exerciseType);

    // FIXME: empty solution!
    if(learnerSolution.isEmpty())
      throw new EmptySolutionException("Sie haben eine leere Query abgegeben!");

    return SqlCorrector.correct(database, learnerSolution, exercise, feedbackLevel);
  }

}
