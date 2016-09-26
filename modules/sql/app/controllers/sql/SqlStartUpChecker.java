package controllers.sql;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import model.SqlScenarioHandler;
import play.Logger;
import play.db.Database;
import play.db.NamedDatabase;

public class SqlStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  
  private static final String SCENARIO_FOLDER = "conf/resources/sql";
  
  private Database sql_main;
  
  @Inject
  public SqlStartUpChecker(@NamedDatabase("sqlselectroot") Database db) {
    sql_main = db;
    performStartUpCheck();
  }
  
  private void performStartUpCheck() {
    Path scenarioDir = Paths.get(SCENARIO_FOLDER);
    
    if(!Files.isDirectory(scenarioDir)) {
      Logger.error("Path " + scenarioDir.toString() + " should be a directory!");
      return;
    }
    
    try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(scenarioDir)) {
      directoryStream.forEach(file -> {
        if(file.getFileName().toString().endsWith("json")) {
          SqlScenarioHandler.handleScenario(file, sql_main);
        }
      });
    } catch (IOException | PersistenceException | NullPointerException e) {
      theLogger.error("Failure: ", e);
    }
  }
  
}
