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
    exercise.setExerciseType(XmlExType.valueOf(form.get(StringConsts.EXERCISE_TYPE)));
    exercise.setRootNode(form.get(StringConsts.ROOT_NODE_NAME));

    final Path referenceFilePath = Paths.get(BaseController.getSampleDir(exerciseType()).toString(),
        exercise.getRootNode() + "." + exercise.getReferenceFileEnding());
    final List<String> referenceFileContent = Arrays
        .asList(form.get(StringConsts.REFERENCE_FILE_CONTENT).split(StringConsts.NEWLINE));

    try {
      Files.write(referenceFilePath, referenceFileContent, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (final IOException e) {
      Logger.error("There has been an error creating a sample xml file", e);
    }
  }

  @Override
  public XmlExercise instantiateExercise(int id) {
    return new XmlExercise(id);
  }

  @Override
  public void saveExercise(XmlExercise exercise) {
    exercise.save();
    Logger.debug(checkOrCreateSampleFile(exercise));
  }

  protected String checkOrCreateSampleFile(XmlExercise exercise) {
    if(!baseTargetDir.toFile().exists() && !createDirectory(baseTargetDir))
      // error occured...
      return "Directory für Lösungsdateien (XML) " + baseTargetDir + "existiert nicht!";

    final String filename = exercise.getRootNode() + "." + exercise.getReferenceFileEnding();

    final Path providedFile = Paths.get("conf", "resources", exerciseType(), filename).toAbsolutePath();
    final Path targetPath = Paths.get(baseTargetDir.toString(), filename).toAbsolutePath();

    if(!providedFile.toFile().exists())
      return "Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...";

    try {
      Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
      return "Die Lösungsdatei wurde erstellt.";
    } catch (final IOException e) {
      Logger.error("Fehler bei Erstellen von Musterlösung " + targetPath, e);
      return "Die Lösungsdatei konnte nicht erstellt werden!";
    }
  }

  @Override
  protected void updateExercise(XmlExercise exercise, JsonNode exerciseNode) {
    exercise.setExerciseType(XmlExType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText()));
    exercise.setRootNode(exerciseNode.get(StringConsts.ROOT_NODE_NAME).asText());
  }

}
