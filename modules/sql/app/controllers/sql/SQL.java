package controllers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.UserManagement;
import model.Secured;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;
import views.html.error;
import views.html.sqlexercise;
import views.html.sqloverview;

@Authenticated(Secured.class)
public class SQL extends Controller {
  
  private static final String SHOW_ALL_TABLES = "SHOW TABLES";
  private static final String SELECT_ALL = "SELECT * FROM ";
  
  @Inject
  @NamedDatabase("sqlselectuser")
  // IMPORTANT: DO NOT USE "DEFAULT" DATABASE
  private Database sql_select;
  
  @Inject
  @NamedDatabase("sqlotherroot")
  private Database sql_other;
  
  @Inject
  private FormFactory factory;
  
  @Inject
  @SuppressWarnings("unused")
  private SqlStartUpChecker checker;
  
  public Result commit(String scenarioName, String exerciseType, int exerciseId) {
    User user = UserManagement.getCurrentUser();

    SqlExerciseType type = SqlExerciseType.valueOf(exerciseType);

    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId, type));
    
    DynamicForm form = factory.form().bindFromRequest();
    
    String learnerSolution = form.get("editorContent");
    FeedbackLevel feedbackLevel = FeedbackLevel.valueOf(form.get("feedbackLevel"));
    
    if(learnerSolution.isEmpty())
      return ok(Json.toJson(Arrays.asList(new EvaluationFailed("Sie haben eine leere Query abgegeben!"))));
    
    if(exercise == null)
      return badRequest(Json.toJson(Arrays.asList(new EvaluationFailed("There is no such exercise!"))));
    
    Database database = getDatabaseForExerciseType(exerciseType);
    
    List<EvaluationResult> result = SqlCorrector.correct(database, user, learnerSolution, exercise, feedbackLevel);
    
    JsonNode ret = Json.toJson(result);
    
    return ok(Json.toJson(ret));
  }
  
  public Result exercise(String scenarioName, String exerciseType, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    
    SqlExerciseType type = SqlExerciseType.valueOf(exerciseType);

    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId, type));
    
    if(exercise == null)
      return badRequest("There is no such exercise!");
    
    try {
      Connection connection = sql_select.getConnection();
      connection.setCatalog(scenarioName);
      List<SqlQueryResult> tables = new LinkedList<>();
      Statement statement = connection.createStatement();
      ResultSet existingDBs = statement.executeQuery(SHOW_ALL_TABLES);
      
      while(existingDBs.next()) {
        String tableName = existingDBs.getString(1);
        Statement selectStatement = connection.createStatement();
        ResultSet tableResult = selectStatement.executeQuery(SELECT_ALL + tableName);
        tables.add(new SqlQueryResult(tableResult, tableName));
        selectStatement.close();
      }
      
      statement.close();
      connection.close();
      return ok(sqlexercise.render(user, exercise, tables));
    } catch (SQLException e) {
      play.Logger.error("Es gab einen Fehler beim Auslesen der Tabellen!", e);
      return badRequest(error.render(user, new Html("Fehler beim Auslesen der Tabellen!")));
    }
  }
  
  public Result index() {
    return ok(sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
  }
  
  private Database getDatabaseForExerciseType(String exerciseType) {
    if(exerciseType.equals("SELECT"))
      return sql_select;
    return sql_other;
  }
  
}
