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

  private static final String BASE_DIR = "conf/resources/spread/";
  private static final String EXERCISE_TYPE = "spread";
  private static final List<String> FILE_ENDINGS = Arrays.asList("xlsx", "ods");

  public static void checkFiles(Util util, SpreadExercise exercise) {
    // Make sure directory exists
    Path sampleFileDirectory = util.getSampleDirForExerciseType(EXERCISE_TYPE);
    if(!Files.exists(sampleFileDirectory)) {
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

  private static void checkFile(Util util, SpreadExercise exercise, String fileName, String fileEnding) {
    Path fileToCheck = util.getSampleFileForExercise(EXERCISE_TYPE, fileName + "." + fileEnding);
    if(Files.exists(fileToCheck))
      return;

    READING_LOGGER.warn("The file \"" + fileToCheck + "\" for spread exercise " + exercise.id
        + " does not exist. Trying to create this file...");

    Path providedFile = Paths.get(BASE_DIR, fileName + "." + fileEnding);
    if(!Files.exists(providedFile)) {
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
    JsonNode idNode = exerciseNode.get("id");
    JsonNode textNode = exerciseNode.get("text");
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode sampleFileNode = exerciseNode.get("sampleFilename");
    JsonNode templateFileNode = exerciseNode.get("templateFilename");

    int id = idNode.asInt();
    SpreadExercise exercise = SpreadExercise.finder.byId(id);
    if(exercise == null)
      exercise = new SpreadExercise(id);

    exercise.text = textNode.asText();
    exercise.title = titleNode.asText();
    exercise.sampleFilename = sampleFileNode.asText();
    exercise.templateFilename = templateFileNode.asText();
    
    return exercise;
  }
}
