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
import play.Logger;

public class SpreadExerciseReader extends ExerciseReader<SpreadExercise> {
  
  private static final List<String> FILE_ENDINGS = Arrays.asList("xlsx", "ods");
  
  public SpreadExerciseReader() {
    super("spread");
  }
  
  @Override
  public void saveExercise(SpreadExercise exercise) {
    exercise.save();
    FILE_ENDINGS.forEach(fileEnding -> {
      checkFile(exercise, exercise.sampleFilename, fileEnding);
      checkFile(exercise, exercise.templateFilename, fileEnding);
    });
  }
  
  protected String checkFile(SpreadExercise exercise, String fileName, String fileEnding) {
    if(!baseTargetDir.toFile().exists() && !createDirectory(baseTargetDir))
      // error occured...
      return "Directory für Lösungsdateien (XML) " + baseTargetDir + "existiert nicht!";
    
    String completeFilename = fileName + "." + fileEnding;
    
    Path providedFile = Paths.get(baseDirForExType.toString(), completeFilename).toAbsolutePath();
    Path targetPath = Paths.get(baseTargetDir.toString(), completeFilename).toAbsolutePath();
    
    if(!providedFile.toFile().exists())
      return "Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...";
    
    READING_LOGGER.warn("The file \"" + targetPath + "\" for " + exerciseType + " exercise " + exercise.id
        + " does not exist. Trying to create this file...");
    
    try {
      Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
      return "Die Lösungsdatei wurde erstellt.";
    } catch (IOException e) {
      Logger.error("Fehler bei Erstellen von Musterlösung " + targetPath, e);
      return "Die Lösungsdatei konnte nicht erstellt werden!";
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
