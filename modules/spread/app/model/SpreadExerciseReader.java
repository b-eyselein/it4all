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
import play.data.DynamicForm;

public class SpreadExerciseReader extends ExerciseReader<SpreadExercise> {
  
  private static final List<String> FILE_ENDINGS = Arrays.asList("xlsx", "ods");
  
  private static final SpreadExerciseReader INSTANCE = new SpreadExerciseReader();
  
  private SpreadExerciseReader() {
    super("spread", SpreadExercise.finder, SpreadExercise[].class);
  }
  
  public static SpreadExerciseReader getInstance() {
    return INSTANCE;
  }
  
  @Override
  public void initRemainingExFromForm(SpreadExercise exercise, DynamicForm form) {
    exercise.setTemplateFilename(form.get(StringConsts.TEMPALTE_FILENAME));
    exercise.setSampleFilename(form.get(StringConsts.SAMPLE_FILENAME));
  }
  
  @Override
  public SpreadExercise instantiateExercise(int id) {
    return new SpreadExercise(id);
  }
  
  @Override
  public void saveExercise(SpreadExercise exercise) {
    exercise.save();
    FILE_ENDINGS.forEach(fileEnding -> {
      checkFile(exercise, exercise.getSampleFilename(), fileEnding);
      checkFile(exercise, exercise.getTemplateFilename(), fileEnding);
    });
  }
  
  protected String checkFile(SpreadExercise exercise, String fileName, String fileEnding) {
    if(!baseTargetDir.toFile().exists() && !CommonUtils$.MODULE$.createDirectory(baseTargetDir))
      // error occured...
      return "Directory für Lösungsdateien (XML) " + baseTargetDir + "existiert nicht!";
    
    final String completeFilename = fileName + "." + fileEnding;
    
    final Path providedFile = Paths.get("conf", "resources", exerciseType(), completeFilename).toAbsolutePath();
    final Path targetPath = Paths.get(baseTargetDir.toString(), completeFilename).toAbsolutePath();
    
    if(!providedFile.toFile().exists())
      return "Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...";
    
    Logger.warn("The file \"" + targetPath + "\" for " + exerciseType() + " exercise " + exercise.getId()
        + " does not exist. Trying to create this file...");
    
    try {
      Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
      return "Die Lösungsdatei wurde erstellt.";
    } catch (final IOException e) {
      Logger.error("Fehler bei Erstellen von Musterlösung " + targetPath, e);
      return "Die Lösungsdatei konnte nicht erstellt werden!";
    }
  }
  
  @Override
  protected void updateExercise(SpreadExercise exercise, JsonNode exerciseNode) {
    exercise.setSampleFilename(exerciseNode.get(StringConsts.SAMPLE_FILENAME).asText());
    exercise.setTemplateFilename(exerciseNode.get(StringConsts.TEMPALTE_FILENAME).asText());
  }
  
}
