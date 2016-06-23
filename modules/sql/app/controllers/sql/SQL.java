package controllers.sql;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.ScriptRunner;
import model.Secured;
import model.SqlCorrectionResult;
import model.SqlCorrector;
import model.SqlExercise;
import model.SqlExercise.SqlExerciseKey;
import model.SqlQueryResult;
import model.SqlScenario;
import model.user.User;
import play.Logger;
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
  
  private static final String DB_BASENAME = "sql_";
  private static final String CREATE_DB_DUMMY = "CREATE DATABASE IF NOT EXISTS ";
  private static final String SHOW_ALL_DBS = "SHOW DATABASES";
  private static final String SHOW_ALL_TABLES = "SHOW TABLES";
  
  private static final Logger.ALogger theLogger = Logger.of("sql");
  
  @Inject
  @NamedDatabase("sqlmain")
  // IMPORTANT: DO NOT USE "DEFAULT" DATABASE
  private Database sql_main;
  
  @Inject
  @NamedDatabase("sqlslave")
  private Database sql_slave;
  
  @Inject
  private FormFactory factory;
  
  public Result commit(String scenarioName, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId));
    
    DynamicForm form = factory.form().bindFromRequest();
    String editorContent = form.get("editorContent");
    
    String slaveDB = DB_BASENAME + user.name;
    
    try {
      Connection connection = sql_slave.getConnection();
      
      initializeDB(connection, slaveDB);
      
      SqlCorrectionResult result = SqlCorrector.correct(editorContent, exercise, connection);
      
      deleteDB(connection, slaveDB);
      
      connection.close();
      
      return ok(Json.prettyPrint(Json.toJson(result)));
    } catch (SQLException e) {
      return badRequest(error.render(user, new Html("Fehler bei Verarbeitung: " + e.getMessage() + "!")));
    }
    
  }
  
  public Result exercise(String scenarioName, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId));
    
    Connection connection = sql_main.getConnection();
    try {
      List<SqlQueryResult> tables = new LinkedList<>();
      ResultSet existingDBs = connection.createStatement().executeQuery(SHOW_ALL_TABLES);
      while(existingDBs.next()) {
        String tableName = existingDBs.getString(1);
        ResultSet tableResult = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
        tables.add(new SqlQueryResult(tableResult, tableName));
      }
      
      connection.close();
      return ok(sqlexercise.render(user, exercise, tables));
    } catch (SQLException e) {
      theLogger.error("Fehler: ", e);
      return badRequest(error.render(user, new Html("Fehler beim Auslesen der Tabellen!")));
    }
  }
  
  public Result index() {
    return ok(sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
  }
  
  private boolean databaseAlreadyExists(Connection connection, String slaveDB) throws SQLException {
    ResultSet existingDBs = connection.createStatement().executeQuery(SHOW_ALL_DBS);
    while(existingDBs.next())
      if(slaveDB.equals(existingDBs.getString(1)))
        return true;
    return false;
  }
  
  private void deleteDB(Connection connection, String slaveDB) {
    // TODO Auto-generated method stub
    long startTime = System.currentTimeMillis();
    try {
      connection.createStatement().executeUpdate("DROP DATABASE IF EXISTS " + slaveDB);
    } catch (SQLException e) {
      theLogger.error("Problems while dropping database " + slaveDB, e);
    }
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully dropped database " + slaveDB + " in " + timeTaken + "ms");
  }
  
  private void initializeDB(Connection connection, String slaveDB) {
    long startTime = System.currentTimeMillis();
    try {
      // Check if slave DB exists, if not, create
      if(!databaseAlreadyExists(connection, slaveDB))
        connection.createStatement().executeUpdate(CREATE_DB_DUMMY + slaveDB);
      
      // Change db to users own db
      connection.setCatalog(slaveDB);
      
      // Initialize DB with values
      Path sqlScriptFile = Paths.get("modules/sql/conf/resources/phone.sql");
      if(Files.exists(sqlScriptFile))
        ScriptRunner.runScript(connection, new FileReader(sqlScriptFile.toFile()), false, false);
      else
        theLogger.error("Trying to initialize database with script " + sqlScriptFile + ", but there is no such file!");
      
    } catch (SQLException | IOException e) {
      theLogger.error("Error while initializing database " + slaveDB, e);
    }
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully initialized the database " + slaveDB + " in " + timeTaken + "ms");
  }
  
}
