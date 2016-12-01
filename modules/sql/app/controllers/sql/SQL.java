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
import model.exercise.SqlExerciseKey;
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
import views.html.correction;
import views.html.sqlexercise;
import views.html.sqloverview;
import play.mvc.Http.Request;

public class SQL extends ExerciseController<SqlExerciseKey> {
  
  private static final String SHOW_ALL_TABLES = "SHOW TABLES";
  
  private static final String SELECT_ALL = "SELECT * FROM ";
  
  private Database sqlSelect;
  
  private Database sqlOther;
  
  @Inject
  public SQL(Util theUtil, FormFactory theFactory, @NamedDatabase("sqlselectuser") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theUtil, theFactory);
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }
  
  public Result commit(String scenarioName, String exerciseType, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExerciseKey key = new SqlExerciseKey(scenarioName, exerciseId, SqlExerciseType.valueOf(exerciseType));
    CompleteResult result = correct(request(), user, key);
    
    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), key, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), key, result));
      return ok(correction.render("SQL", result, user));
    }
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, SqlExerciseKey identifier) {
    SqlExercise exercise = SqlExercise.finder.byId(identifier);
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    FeedbackLevel feedbackLevel = FeedbackLevel.valueOf(form.get("feedbackLevel"));
    
    // FIXME: Speichern der Lösung?!?
    
    Database database = getDatabaseForExerciseType(exercise.key.exercisetype);
    
    if(learnerSolution.isEmpty())
      return new SqlCorrectionResult(learnerSolution,
          Arrays.asList(new EvaluationFailed("Sie haben eine leere Query abgegeben!")));
    
    return SqlCorrector.correct(database, learnerSolution, exercise, feedbackLevel);
  }
  
  public Result exercise(String scenarioName, String exerciseType, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    
    SqlExerciseType type = SqlExerciseType.valueOf(exerciseType);
    SqlExerciseKey key = new SqlExerciseKey(scenarioName, exerciseId, type);
    SqlExercise exercise = SqlExercise.finder.byId(key);
    
    if(exercise == null)
      return badRequest("There is no such exercise!");
    
    List<SqlQueryResult> tables = readTablesInDatabase(scenarioName);
    
    log(user, new ExerciseStartEvent(request(), key));
    
    return ok(sqlexercise.render(user, exercise, tables));
  }
  
  private Database getDatabaseForExerciseType(SqlExerciseType exerciseType) {
    if(exerciseType == SqlExerciseType.SELECT)
      return sqlSelect;
    return sqlOther;
  }
  
  public Result index() {
    return ok(sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
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
