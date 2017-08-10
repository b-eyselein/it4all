package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import play.Logger;

public class XmlExerciseReader extends ExerciseReader<XmlExercise> {

  private static final XmlExerciseReader INSTANCE = new XmlExerciseReader();

  private XmlExerciseReader() {
    super("xml", XmlExercise.finder, XmlExercise[].class);
  }

  public static XmlExerciseReader getInstance() {
    return INSTANCE;
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
  protected XmlExercise instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    String fixedStart = readTextArray(exerciseNode.get(StringConsts.FIXED_START), "\n");
    XmlExType exerciseType = XmlExType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());
    String referenceFileName = exerciseNode.get(StringConsts.REFERENCE_FILE_NAME).asText();

    return new XmlExercise(id, title, author, text, fixedStart, exerciseType, referenceFileName);
  }

}
