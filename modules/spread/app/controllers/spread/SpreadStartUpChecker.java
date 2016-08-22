package controllers.spread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import model.Util;
import model.spread.SpreadExercise;
import play.Logger;
import play.libs.Json;

public class SpreadStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  
  private static final String baseDir = "conf/resources/spread/";
  private static final String EXERCISE_TYPE = "spread";
  private static final List<String> FILE_ENDINGS = Arrays.asList("xlsx", "ods");
  
  private Util util;
  
  @Inject
  public SpreadStartUpChecker(Util theUtil) {
    util = theUtil;
    performStartUpCheck();
  }
  
  public void performStartUpCheck() {
    Path exerciseFile = Paths.get(baseDir, "exercises.json");
    if(!Files.exists(exerciseFile)) {
      theLogger.error("Exercise file for Spread not found!");
      return;
    }
    
    String fileContent = "";
    try {
      fileContent = String.join("\n", Files.readAllLines(exerciseFile));
    } catch (IOException e) {
      theLogger.error("Could not read exercise file for Spread!", e);
      return;
    }
    
    JsonNode document = Json.parse(fileContent);
    
    for(final Iterator<JsonNode> exerciseNodes = document.elements(); exerciseNodes.hasNext();) {
      JsonNode exerciseNode = exerciseNodes.next();
      int id = exerciseNode.get("id").asInt();
      
      SpreadExercise exercise = SpreadExercise.finder.byId(id);
      if(exercise == null)
        exercise = new SpreadExercise(id);
      exercise.text = exerciseNode.get("text").asText();
      exercise.title = exerciseNode.get("title").asText();
      exercise.sampleFilename = exerciseNode.get("sampleFilename").asText();
      exercise.templateFilename = exerciseNode.get("templateFilename").asText();
      exercise.save();
      
      checkFiles(exercise);
      
    }
    
  }
  
  private void checkFile(SpreadExercise exercise, String fileName, String fileEnding) {
    Path fileToCheck = util.getSampleFileForExercise(EXERCISE_TYPE, fileName + "." + fileEnding);
    if(Files.exists(fileToCheck))
      return;
    
    theLogger.warn("The file \"" + fileToCheck + "\" for spread exercise " + exercise.id
        + " does not exist. Trying to create this file...");
    
    Path providedFile = Paths.get(baseDir, fileName + "." + fileEnding);
    if(!Files.exists(providedFile)) {
      theLogger.error("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert in " + providedFile);
      return;
    }
    
    try {
      Files.copy(providedFile, fileToCheck, StandardCopyOption.REPLACE_EXISTING);
      theLogger.info("Die Lösungsdatei wurde erstellt.");
    } catch (IOException e) {
      theLogger.error("Die Lösungsdatei konnte nicht erstellt werden!", e);
    }
  }
  
  private void checkFiles(SpreadExercise exercise) {
    // Make sure directory exists
    Path sampleFileDirectory = util.getSampleDirForExerciseType(EXERCISE_TYPE);
    if(!Files.exists(sampleFileDirectory)) {
      try {
        Files.createDirectories(sampleFileDirectory);
      } catch (IOException e) {
        theLogger.error("Could not create directory for sample files for spread exercise " + exercise.id + "!", e);
        return;
      }
    }
    
    // Make sure files exist, copy to directory if not
    for(String fileEnding: FILE_ENDINGS) {
      checkFile(exercise, exercise.sampleFilename, fileEnding);
      checkFile(exercise, exercise.templateFilename, fileEnding);
    }
  }
  
}
