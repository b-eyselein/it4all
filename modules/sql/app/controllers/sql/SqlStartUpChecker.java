package controllers.sql;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import model.creation.ExerciseCreationResult;
import model.creation.ScenarioCreationResult;
import model.creation.SqlScenarioHandler;
import play.Logger;

public class SqlStartUpChecker {

  private static Logger.ALogger theLogger = Logger.of("startup");

  private static final String SCENARIO_FOLDER = "conf/resources/sql";

  @Inject
  public SqlStartUpChecker() {
    performStartUpCheck();
  }
  
  private void createScenario(ScenarioCreationResult result) {
    // TODO Auto-generated method stub
    result.getCreated().save();
    for(ExerciseCreationResult exercise: result.getExerciseResults())
      if(exercise.getCreated() != null)
        exercise.getCreated().save();
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
          ScenarioCreationResult result = SqlScenarioHandler.handleScenario(file);
          createScenario(result);
        }
      });
    } catch (IOException | PersistenceException | NullPointerException e) {
      theLogger.error("Failure: ", e);
    }
  }

}
