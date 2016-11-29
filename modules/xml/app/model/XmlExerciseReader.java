package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.fasterxml.jackson.databind.JsonNode;

import model.XmlExercise.XmlExType;
import model.exercisereading.ExerciseReader;

public class XmlExerciseReader extends ExerciseReader<XmlExercise> {

  private static final String EXERCISE_TYPE = "xml";
  private static final String BASE_DIR = "conf/resources/xml/";

  public void checkOrCreateSampleFile(Util util, XmlExercise exercise) {
    // FIXME: test on clean instance without folder /var/lib/it4all...
    Path sampleFile = Paths.get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString()
        + exercise.getReferenceFileEnding());
    if(sampleFile.toFile().exists())
      return;

    READING_LOGGER.warn("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile
        + "\" existiert nicht! Versuche, Datei zu erstellen...");

    Path providedFile = Paths.get(BASE_DIR, exercise.referenceFileName + exercise.getReferenceFileEnding());
    if(!Files.exists(providedFile)) {
      READING_LOGGER.error("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...");
      return;
    }

    try {
      Files.copy(providedFile, sampleFile, StandardCopyOption.REPLACE_EXISTING);
      READING_LOGGER.info("Die Lösungsdatei wurde erstellt.");
    } catch (IOException e) {
      READING_LOGGER.error("Die Lösungsdatei konnte nicht erstellt werden!", e);
    }
  }

  @Override
  protected XmlExercise readExercise(JsonNode node) {
    JsonNode idNode = node.get("id");
    int id = idNode.asInt();

    XmlExercise exercise = XmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new XmlExercise();

    exercise.id = id;
    exercise.title = node.get("title").asText();
    exercise.exerciseType = XmlExType.valueOf(node.get("exerciseType").asText());
    exercise.referenceFileName = node.get("referenceFileName").asText();
    exercise.exerciseText = node.get("exerciseText").asText();
    exercise.fixedStart = node.get("fixedStart").asText();
    return exercise;
  }

}
