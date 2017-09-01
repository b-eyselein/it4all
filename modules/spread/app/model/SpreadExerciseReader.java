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

  public static SpreadExerciseReader getInstance() {
    return INSTANCE;
  }

  private SpreadExerciseReader() {
    super("spread", SpreadExercise.finder, SpreadExercise[].class);
  }

  @Override
  public void initRemainingExFromForm(SpreadExercise exercise, DynamicForm form) {
    exercise.setTemplateFilename(form.get(StringConsts.TEMPALTE_FILENAME));
    exercise.setSampleFilename(form.get(StringConsts.SAMPLE_FILENAME));
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
    if(!baseTargetDir.toFile().exists() && !createDirectory(baseTargetDir))
      // error occured...
      return "Directory für Lösungsdateien (XML) " + baseTargetDir + "existiert nicht!";

    String completeFilename = fileName + "." + fileEnding;

    Path providedFile = Paths.get("conf", "resources", exerciseType, completeFilename).toAbsolutePath();
    Path targetPath = Paths.get(baseTargetDir.toString(), completeFilename).toAbsolutePath();

    if(!providedFile.toFile().exists())
      return "Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...";

    Logger.warn("The file \"" + targetPath + "\" for " + exerciseType + " exercise " + exercise.getId()
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
  protected SpreadExercise instantiateExercise(int id) {
    return new SpreadExercise(id);
  }

  @Override
  protected void updateExercise(SpreadExercise exercise, JsonNode exerciseNode) {
    exercise.setSampleFilename(exerciseNode.get(StringConsts.SAMPLE_FILENAME).asText());
    exercise.setTemplateFilename(exerciseNode.get(StringConsts.TEMPALTE_FILENAME).asText());
  }

}
