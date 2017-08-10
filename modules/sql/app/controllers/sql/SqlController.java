package controllers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.CorrectionException;
import model.Levenshtein;
import model.SqlSolution;
import model.SqlSolutionKey;
import model.SqlUser;
import model.StringConsts;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;
import model.exercise.SqlSample;
import model.exercise.SqlScenario;
import model.logging.ExerciseStartEvent;
import model.querycorrectors.SqlResult;
import model.sql.SqlQueryResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlController extends ExerciseController<SqlExercise, SqlResult> {

  private static final String STANDARD_SQL = "";

  private Database sqlSelect;
  private Database sqlOther;

  @Inject
  public SqlController(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, "sql", SqlExercise.finder);
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

  private static void saveSolution(String userName, String learnerSolution, int id) {
    SqlSolutionKey key = new SqlSolutionKey(userName, id);

    SqlSolution solution = SqlSolution.finder.byId(key);
    if(solution == null)
      solution = new SqlSolution(key);

    solution.sol = learnerSolution;
    solution.save();
  }

  public Result exercise(int id) {
    User user = getUser();
    
    SqlExercise exercise = SqlExercise.finder.byId(id);
    
    if(exercise == null)
      return redirect(controllers.sql.routes.SqlController.index());
    
    List<SqlQueryResult> tables = readTablesInDatabase(sqlSelect,
        "" /* exercise.scenario.shortName */);
    
    SqlSolution oldSol = SqlSolution.finder.byId(new SqlSolutionKey(user.name, id));
    String oldOrDefSol = oldSol == null ? STANDARD_SQL : oldSol.sol;
    
    log(user, new ExerciseStartEvent(request(), id));
    
    return ok(views.html.sqlExercise.render(user, exercise, oldOrDefSol, tables));

  }

  public Result filteredScenario(int id, String exType, int start) {
    final SqlScenario scenario = SqlScenario.finder.byId(id);

    if(scenario == null)
      return redirect(controllers.sql.routes.SqlController.index());

    return ok(views.html.sqlscenario.render(getUser(), scenario, SqlExerciseType.valueOf(exType), start));
  }

  public Result index() {
    return ok(views.html.sqlIndex.render(getUser(), SqlScenario.finder.all()));
  }

  public Result scenarioes() {
    return ok(views.html.scenarioes.render(getUser(), SqlScenario.finder.all()));
  }

  private Database getDBForExType(SqlExerciseType exerciseType) {
    return exerciseType == SqlExerciseType.SELECT ? sqlSelect : sqlOther;
  }

  @Override
  protected List<SqlResult> correct(String learnerSolution, SqlExercise exercise, User user)
      throws CorrectionException {
    String learnerSol = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);

    if(learnerSol == null || learnerSol.isEmpty())
      throw new CorrectionException(learnerSol, StringConsts.EMPTY_SOLUTION);

    saveSolution(user.name, learnerSol, exercise.getId());

    SqlSample sample = findBestFittingSample(learnerSol, exercise.samples);

    return Arrays
        .asList(exercise.getCorrector().correct(getDBForExType(exercise.exerciseType), learnerSol, sample, exercise));
  }

  @Override
  protected Html renderResult(List<SqlResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }
}
