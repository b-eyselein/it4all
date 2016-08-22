package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import model.XmlExercise.XmlExType;
import model.Util;
import model.XmlExercise;
import play.Logger;
import play.libs.Json;

public class XmlStartUpChecker {

  private static Logger.ALogger theLogger = Logger.of("startup");
  private static final String EXERCISE_TYPE = "xml";

  private static final String baseDir = "conf/resources/xml/";

  private Util util;

  @Inject
  public XmlStartUpChecker(Util theUtil) {
    util = theUtil;
    performStartUpCheck();

    testJsonCreation();
  }

  public void performStartUpCheck() {
    Path sampleDir = util.getSampleDirForExercise(EXERCISE_TYPE);
    if(!Files.exists(sampleDir)) {
      try {
        Files.createDirectories(sampleDir);
      } catch (IOException e) {
        theLogger.error("Could not create directory for xml samples!", e);
      }
    }

    List<XmlExercise> exercises = XmlExercise.finder.all();
    if(exercises.size() == 0)
      theLogger.error("\t- No exercises found for Xml!");
    else
      for(XmlExercise exercise: exercises)
        checkOrCreateSampleFile(exercise);
  }

  private void checkOrCreateSampleFile(XmlExercise exercise) {
    Path sampleFile = util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName);
    if(Files.exists(sampleFile))
      return;

    theLogger.warn("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile
        + "\" existiert nicht! Versuche, Datei zu erstellen...");

    Path providedFile = Paths.get(baseDir, exercise.referenceFileName);
    if(!Files.exists(providedFile)) {
      theLogger.error("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...");
      return;
    }

    try {
      Files.copy(providedFile, sampleFile, StandardCopyOption.REPLACE_EXISTING);
      theLogger.info("Die Lösungsdatei wurde erstellt.");
    } catch (IOException e) {
      theLogger.error("Die Lösungsdatei konnte nicht erstellt werden!", e);
    }
  }

  private void testJsonCreation() {
    Path exercises = Paths.get(baseDir, "exercises.json");

    String jsonAsString = "";
    try {
      jsonAsString = String.join("\n", Files.readAllLines(exercises));
    } catch (IOException e) {
      theLogger.error("Fehler beim Lesen aus der Datei " + exercises.toString(), e);
    }

    if(jsonAsString.isEmpty())
      return;

    JsonNode json = Json.parse(jsonAsString);
    for(final Iterator<JsonNode> childNodes = json.elements(); childNodes.hasNext();) {
      JsonNode node = childNodes.next();
      int id = node.get("id").asInt();

      XmlExercise exercise = XmlExercise.finder.byId(id);
      if(exercise == null)
        exercise = new XmlExercise();

      exercise.id = id;
      exercise.title = node.get("title").asText();
      exercise.exerciseType = XmlExType.valueOf(node.get("exerciseType").asText());
      exercise.referenceFileName = node.get("referenceFileName").asText();
      exercise.exerciseText = node.get("exerciseText").asText();

      exercise.save();

    }
  }

}
