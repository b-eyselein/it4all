package controllers.sql;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.ScriptRunner;
import model.Secured;
import model.SqlExercise;
import model.user.User;
import play.Logger;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;
import views.html.error;
import views.html.sql;
import views.html.sqlexercise;

@Authenticated(Secured.class)
public class SQL extends Controller {
  
  private static final String DB_BASENAME = "sql_";
  private static final String CREATE_DB_DUMMY = "CREATE DATABASE IF NOT EXISTS ";
  
  private static final Logger.ALogger theLogger = Logger.of("sql");
  
  @Inject
  @NamedDatabase("sqltest")
  // IMPORTANT: DO NOT USE "DEFAULT" DATABASE
  private Database db;
  
  public Result exercise(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.byId(exerciseId);
    return ok(sqlexercise.render(user, exercise));
  }
  
  public Result index() {
    User user = UserManagement.getCurrentUser();
    try {
      String slaveDB = DB_BASENAME + user.name;
      Connection connection = db.getConnection();
      
      initializeDB(connection, slaveDB);
      connection.close();
      
      return ok(sql.render(UserManagement.getCurrentUser(), SqlExercise.all()));
    } catch (SQLException e) {
      return badRequest(error.render(user, new Html("Fehler bei Verarbeitung: " + e.getMessage() + "!")));
    }
  }
  
  private boolean databaseAlreadyExists(Connection connection, String slaveDB) throws SQLException {
    ResultSet existingDBs = connection.createStatement().executeQuery("SHOW DATABASES");
    while(existingDBs.next())
      if(slaveDB.equals(existingDBs.getString(1)))
        return true;
    return false;
  }
  
  private void initializeDB(Connection connection, String slaveDB) {
    long startTime = System.currentTimeMillis();
    try {
      // Check if slave DB exists, if not, create
      if(!databaseAlreadyExists(connection, slaveDB))
        connection.createStatement().executeUpdate(CREATE_DB_DUMMY + slaveDB);
      
      // Change db to users own db
      if(!connection.getCatalog().equals(slaveDB))
        connection.setCatalog(slaveDB);
      
      // Initialize DB with values
      Path sqlScriptFile = Paths.get("modules/sql/conf/resources/phone.sql");
      if(Files.exists(sqlScriptFile))
        ScriptRunner.runScript(connection, new FileReader(sqlScriptFile.toFile()), false, false);
      else
        theLogger.debug("There is no such file " + sqlScriptFile);
      
    } catch (SQLException | IOException e) {
      theLogger.error("Error while initializing database " + slaveDB, e);
    }
    theLogger.debug("time initializing the db: " + (System.currentTimeMillis() - startTime) / 1000.);
  }
  
}
