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
    super("xml");
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

    Path providedFile = Paths.get(baseDirForExType.toString(), filename).toAbsolutePath();
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
  protected XmlExercise read(JsonNode node) {
    int id = node.get(StringConsts.ID_NAME).asInt();

    String title = node.get(StringConsts.TITLE_NAME).asText();
    String author = node.get(StringConsts.AUTHOR_NAME).asText();
    String text = JsonWrapper.readTextArray(node.get(StringConsts.TEXT_NAME), "");

    String fixedStart = String.join("\n", JsonWrapper.parseJsonArrayNode(node.get(StringConsts.FIXED_START)));
    XmlExType exerciseType = XmlExType.valueOf(node.get(StringConsts.EXERCISE_TYPE).asText());
    String referenceFileName = node.get(StringConsts.REFERENCE_FILE_NAME).asText();

    XmlExercise exercise = XmlExercise.finder.byId(id);
    if(exercise == null)
      return new XmlExercise(id, title, author, text, fixedStart, exerciseType, referenceFileName);
    else
      return exercise.updateValues(id, title, author, text, fixedStart, exerciseType, referenceFileName);
  }

}
