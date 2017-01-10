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
  
  public XmlExerciseReader() {
    super("xml");
  }
  
  public void checkOrCreateSampleFile(Util util, XmlExercise exercise) {
    createSampleDirectory(util);
    
    Path sampleFile = Paths.get(util.getSampleFileForExercise(exerciseType, exercise.referenceFileName).toString()
        + exercise.getReferenceFileEnding());
    if(sampleFile.toFile().exists())
      return;

    READING_LOGGER.warn("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile
        + "\" existiert nicht! Versuche, Datei zu erstellen...");
    
    Path providedFile = Paths.get(BASE_DIR, exerciseType,
        exercise.referenceFileName + exercise.getReferenceFileEnding());
    
    if(!providedFile.toFile().exists()) {
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
      exercise = new XmlExercise(id);

    exercise.id = id;
    exercise.title = node.get("title").asText();
    exercise.exerciseType = XmlExType.valueOf(node.get("exerciseType").asText());
    exercise.referenceFileName = node.get("referenceFileName").asText();
    exercise.text = node.get("exerciseText").asText();
    exercise.fixedStart = node.get("fixedStart").asText();
    return exercise;
  }

}
