package controllers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.Levenshtein;
import model.SqlSolution;
import model.SqlSolutionKey;
import model.SqlUser;
import model.StringConsts;
import model.correction.CorrectionException;
import model.correction.EmptySolutionException;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlSample;
import model.exercise.SqlScenario;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.querycorrectors.SqlResult;
import model.sql.SqlQueryResult;
import model.user.User;
import net.sf.jsqlparser.statement.Statement;
import play.Logger;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;

public class Sql extends ExerciseController {
  
  private static final String STANDARD_SQL = "";
  
  private Database sqlSelect;
  private Database sqlOther;
  
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
  
  private static SqlSample findBestFittingSample(String userStatement, List<SqlSample> samples) {
    return samples.parallelStream().min((samp1, samp2) -> {
      int dist1 = Levenshtein.distance(samp1.sample, userStatement);
      int dist2 = Levenshtein.distance(samp2.sample, userStatement);
      return dist1 - dist2;
    }).orElse(samples.get(0));
  }
  
  private static List<String> readExistingTables(Connection connection) {
    try(ResultSet existingTables = connection.createStatement().executeQuery(StringConsts.SHOW_ALL_TABLES)) {
      final List<String> tableNames = new LinkedList<>();
      while(existingTables.next())
        tableNames.add(existingTables.getString(1));
      return tableNames;
    } catch (SQLException e) {
      Logger.error("There was an error reading the table names from a database", e);
      return Collections.emptyList();
    }
  }
  
  private static SqlQueryResult readTableContent(Connection connection, String tableName) {
    try(PreparedStatement selectStatement = connection.prepareStatement(StringConsts.SELECT_ALL_DUMMY + tableName)) { // NOSONAR
      final ResultSet tableResult = selectStatement.executeQuery();
      return new SqlQueryResult(tableResult, tableName);
    } catch (SQLException e) {
      Logger.error("There has been an error reading the data from the table " + tableName, e);
      return null;
    }
  }
  
  private static List<SqlQueryResult> readTablesInDatabase(Database db, String databaseName) {
    try(Connection connection = db.getConnection()) {
      connection.setCatalog(databaseName);
      return readExistingTables(connection).stream().map(tableName -> readTableContent(connection, tableName))
          .collect(Collectors.toList());
    } catch (SQLException e) {
      Logger.error("Es gab einen Fehler beim Auslesen der Tabellen!", e);
      return Collections.emptyList();
    }
  }
  
  private static void saveSolution(String userName, String learnerSolution, SqlExerciseKey exKey) {
    SqlSolutionKey key = new SqlSolutionKey(userName, exKey);
    
    SqlSolution solution = SqlSolution.finder.byId(key);
    if(solution == null)
      solution = new SqlSolution(key);
    
    solution.sol = learnerSolution;
    solution.save();
  }
  
  public Result correct(int scenarioId, int exerciseId) {
    User user = getUser();
    try {
      SqlResult<? extends Statement, ?> result = correct(user.name, scenarioId, exerciseId);
      
      log(user, new ExerciseCompletionEvent(request(), exerciseId, result.getResults()));
      
      return ok(
          views.html.correction.render("SQL", result.render(), result.getLearnerSolution(), user, routes.Sql.index()));
    } catch (CorrectionException e) { // NOSONAR
      return ok(
          views.html.error.render(user, "<div class=\"alert alert-danger\">Es gab einen Fehler bei der Korrektur: <pre>"
              + e.getStackTrace() + "</pre></div>"));
    }
  }
  
  public Result correctLive(int scenarioId, int exerciseId) {
    try {
      User user = getUser();

      SqlResult<? extends Statement, ?> result = correct(user.name, scenarioId, exerciseId);

      log(user, new ExerciseCorrectionEvent(request(), exerciseId, result.getResults()));

      return ok(result.render());
    } catch (CorrectionException e) { // NOSONAR
      return ok(views.html.correctionerror
          .render("<p>Es gab einen Fehler bei der Korrektur:</p><pre>" + e.getMessage() + "</pre>"));
    }
  }
  
  public Result exercise(int scenarioId, int exerciseId) {
    User user = getUser();
    
    SqlExerciseKey exKey = new SqlExerciseKey(scenarioId, exerciseId);
    SqlExercise exercise = SqlExercise.finder.byId(exKey);
    
    if(exercise == null)
      return redirect(controllers.sql.routes.Sql.index());
    
    List<SqlQueryResult> tables = readTablesInDatabase(sqlSelect, exercise.scenario.shortName);
    
    SqlSolution oldSol = SqlSolution.finder.byId(new SqlSolutionKey(user.name, exKey));
    String oldOrDefSol = oldSol == null ? STANDARD_SQL : oldSol.sol;
    
    log(user, new ExerciseStartEvent(request(), exerciseId));
    
    return ok(views.html.sqlExercise.render(user, exercise, oldOrDefSol, tables));
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
  
  private SqlResult<? extends Statement, ?> correct(String userName, int scenarioId, int exerciseId)
      throws CorrectionException {
    String learnerSol = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);

    if(learnerSol == null || learnerSol.isEmpty())
      throw new EmptySolutionException(learnerSol, "Sie haben eine leere Lösung abgegeben!");

    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioId, exerciseId));

    saveSolution(userName, learnerSol, exercise.key);

    if(learnerSol.isEmpty())
      throw new EmptySolutionException(learnerSol, StringConsts.EMPTY_SOLUTION);

    SqlSample sample = findBestFittingSample(learnerSol, exercise.samples);

    return exercise.getCorrector().correct(getDBForExType(exercise.exerciseType), learnerSol, sample, exercise);
  }
  
  private Database getDBForExType(SqlExerciseType exerciseType) {
    return exerciseType == SqlExerciseType.SELECT ? sqlSelect : sqlOther;
  }
}
