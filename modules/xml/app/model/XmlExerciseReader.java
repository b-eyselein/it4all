package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.BaseController;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.data.DynamicForm;

public class XmlExerciseReader extends ExerciseReader<XmlExercise> {

  private static final XmlExerciseReader INSTANCE = new XmlExerciseReader();

  private XmlExerciseReader() {
    super("xml", XmlExercise.finder, XmlExercise[].class);
  }

  public static XmlExerciseReader getInstance() {
    return INSTANCE;
  }

  @Override
  public void initRemainingExFromForm(XmlExercise exercise, DynamicForm form) {
    exercise.setFixedStart(form.get(StringConsts.FIXED_START));
    exercise.setExerciseType(XmlExType.valueOf(form.get(StringConsts.EXERCISE_TYPE)));
    exercise.setReferenceFileName(form.get(StringConsts.REFERENCE_FILE_NAME));

    Path referenceFilePath = Paths.get(BaseController.getSampleDir(this.exerciseType).toString(),
        exercise.getReferenceFileName() + "." + exercise.getReferenceFileEnding());
    List<String> referenceFileContent = Arrays
        .asList(form.get(StringConsts.REFERENCE_FILE_CONTENT).split(StringConsts.NEWLINE));

    try {
      Files.write(referenceFilePath, referenceFileContent, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      Logger.error("There has been an error creating a sample xml file", e);
    }
  }

  @Override
  public void saveRead(XmlExercise exercise) {
    exercise.save();
    Logger.debug(checkOrCreateSampleFile(exercise));
  }

  protected String checkOrCreateSampleFile(XmlExercise exercise) {
    if(!baseTargetDir.toFile().exists() && !createDirectory(baseTargetDir))
      // error occured...
      return "Directory für Lösungsdateien (XML) " + baseTargetDir + "existiert nicht!";

    String filename = exercise.getReferenceFileName() + "." + exercise.getReferenceFileEnding();

    Path providedFile = Paths.get("conf", "resources", exerciseType, filename).toAbsolutePath();
    Path targetPath = Paths.get(baseTargetDir.toString(), filename).toAbsolutePath();

    if(!providedFile.toFile().exists())
      return "Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...";

    try {
      Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
      return "Die Lösungsdatei wurde erstellt.";
    } catch (IOException e) {
      Logger.error("Fehler bei Erstellen von Musterlösung " + targetPath, e);
      return "Die Lösungsdatei konnte nicht erstellt werden!";
    }
  }

  @Override
  protected XmlExercise instantiateExercise(int id) {
    return new XmlExercise(id);
  }

  @Override
  protected void updateExercise(XmlExercise exercise, JsonNode exerciseNode) {
    exercise.setFixedStart(readTextArray(exerciseNode.get(StringConsts.FIXED_START), "\n"));
    exercise.setExerciseType(XmlExType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText()));
    exercise.setReferenceFileName(exerciseNode.get(StringConsts.REFERENCE_FILE_NAME).asText());
  }

}
