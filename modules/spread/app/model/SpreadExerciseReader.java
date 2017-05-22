package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class SpreadExerciseReader extends ExerciseReader<SpreadExercise> {
  
  private static final List<String> FILE_ENDINGS = Arrays.asList("xlsx", "ods");
  
  public SpreadExerciseReader() {
    super("spread");
  }
  
  public void checkFiles(Util util, SpreadExercise exercise) {
    // Make sure directory exists
    Path sampleFileDirectory = util.getSampleDirForExerciseType(exerciseType);
    if(!sampleFileDirectory.toFile().exists()) {
      try {
        Files.createDirectories(sampleFileDirectory);
      } catch (IOException e) {
        READING_LOGGER.error("Could not create directory for sample files for spread exercise " + exercise.id + "!", e);
        return;
      }
    }
    
    // Make sure files exist, copy to directory if not
    for(String fileEnding: FILE_ENDINGS) {
      checkFile(util, exercise, exercise.sampleFilename, fileEnding);
      checkFile(util, exercise, exercise.templateFilename, fileEnding);
    }
  }
  
  private void checkFile(Util util, SpreadExercise exercise, String fileName, String fileEnding) {
    if(!baseTargetDir.toFile().exists() && !createSampleDirectory())
      return;
    
    Path fileToCheck = util.getSampleFileForExercise(exerciseType, fileName + "." + fileEnding);
    if(fileToCheck.toFile().exists())
      return;
    
    READING_LOGGER.warn("The file \"" + fileToCheck + "\" for spread exercise " + exercise.id
        + " does not exist. Trying to create this file...");
    
    Path providedFile = Paths.get(BASE_DIR, exerciseType, fileName + "." + fileEnding);
    if(!providedFile.toFile().exists()) {
      READING_LOGGER.error("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert in " + providedFile);
      return;
    }
    
    try {
      Files.copy(providedFile, fileToCheck, StandardCopyOption.REPLACE_EXISTING);
      READING_LOGGER.info("Die Lösungsdatei wurde erstellt.");
    } catch (IOException e) {
      READING_LOGGER.error("Die Lösungsdatei konnte nicht erstellt werden!", e);
    }
  }
  
  @Override
  protected SpreadExercise readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();
    
    SpreadExercise exercise = SpreadExercise.finder.byId(id);
    if(exercise == null)
      exercise = new SpreadExercise(id);
    
    exercise.text = exerciseNode.get(StringConsts.TEXT_NAME).asText();
    exercise.title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    exercise.sampleFilename = exerciseNode.get("sampleFilename").asText();
    exercise.templateFilename = exerciseNode.get("templateFilename").asText();
    
    return exercise;
  }
}
