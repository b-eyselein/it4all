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
import model.Levenshtein;
import model.SqlCorrectionException;
import model.SqlSolution;
import model.SqlSolutionKey;
import model.SqlUser;
import model.StringConsts;
import model.correction.CorrectionException;
import model.correction.EmptySolutionException;
import model.correctionresult.SqlResult;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlSample;
import model.exercise.SqlScenario;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.querycorrectors.QueryCorrector;
import model.sql.SqlQueryResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;

public class Sql extends ExerciseController {

  private static final String SHOW_ALL_TABLES = "SHOW TABLES";
  private static final String SELECT_ALL_DUMMY = "SELECT * FROM ";

  public Database sqlSelect;
  public Database sqlOther;

  @Inject
  public Sql(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, "sql");
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  public static User getUser() {
    User user = ExerciseController.getUser();

    if(SqlUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new SqlUser(user.name).save();

    return user;
  }

  private static SqlSample findBestFittingSample(final String userStatement, final List<SqlSample> samples) {
    return samples.parallelStream().min((samp1, samp2) -> {
      int dist1 = Levenshtein.levenshteinDistance(samp1.sample, userStatement);
      int dist2 = Levenshtein.levenshteinDistance(samp2.sample, userStatement);
      return dist1 - dist2;
    }).orElse(samples.get(0));
  }

  public Result correct(int scenarioId, int exerciseId) {
    final User user = getUser();
    final SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioId, exerciseId));

    final String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);

    SqlResult result;
    try {
      result = correct(user.name, learnerSolution, exercise);
    } catch (CorrectionException e) { // NOSONAR
      return ok(
          views.html.error.render(user, "<div class=\"alert alert-danger\">Es gab einen Fehler bei der Korrektur: <pre>"
              + e.getStackTrace() + "</pre></div>"));
    }

    log(user, new ExerciseCompletionEvent(request(), exerciseId, result.getResults()));
    return ok(views.html.correction.render("SQL", views.html.sqlResult.render(result), learnerSolution, user));
  }

  public Result correctLive(int scenarioId, int exerciseId) {
    final User user = getUser();
    final SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioId, exerciseId));

    final String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);

    SqlResult result;
    try {
      result = correct(user.name, learnerSolution, exercise);
    } catch (CorrectionException e) { // NOSONAR
      return ok(views.html.correctionerror.render("Sie haben eine leere L&ouml;sung abgegeben!"));
    }

    log(user, new ExerciseCorrectionEvent(request(), exerciseId, result.getResults()));
    return ok(views.html.sqlResult.render(result));
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
    try(PreparedStatement selectStatement = connection.prepareStatement(SELECT_ALL_DUMMY + tableName);) {
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

  private void saveSolution(String userName, String learnerSolution, SqlExerciseKey exKey) {
    SqlSolutionKey key = new SqlSolutionKey(userName, exKey);

    SqlSolution solution = SqlSolution.finder.byId(key);
    if(solution == null)
      solution = new SqlSolution(key);

    solution.sol = learnerSolution;
    solution.save();
  }

  protected SqlResult correct(String userName, String learnerSolution, SqlExercise exercise)
      throws CorrectionException {
    saveSolution(userName, learnerSolution, exercise.key);

    Database database = getDatabaseForExerciseType(exercise.exerciseType);

    if(learnerSolution.isEmpty())
      throw new EmptySolutionException(learnerSolution, "Sie haben eine leere Query abgegeben!");

    QueryCorrector<? extends net.sf.jsqlparser.statement.Statement, ?> corrector = exercise.getCorrector();

    SqlSample sampleStatement = findBestFittingSample(learnerSolution, exercise.samples);

    return corrector.correct(database, learnerSolution, sampleStatement, exercise);
  }
}
