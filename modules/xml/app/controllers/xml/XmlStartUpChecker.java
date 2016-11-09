package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.inject.Inject;

import model.Util;
import model.XmlExercise;
import model.XmlExerciseReader;
import play.Logger;

public class XmlStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  private static final String EXERCISE_TYPE = "xml";
  
  private static final String BASE_DIR = "conf/resources/xml/";
  
  private Util util;
  
  @Inject
  public XmlStartUpChecker(Util theUtil) {
    util = theUtil;
    performStartUpCheck();
    
    Path jsonPath = Paths.get(BASE_DIR, "exercises.json");
    Path jsonSchemaPath = Paths.get(BASE_DIR, "exerciseSchema.json");

    List<XmlExercise> exercises = (new XmlExerciseReader()).readExercises(jsonPath, jsonSchemaPath);
    for(XmlExercise ex: exercises) {
      ex.save();
      checkOrCreateSampleFile(ex);
    }
  }
  
  public void performStartUpCheck() {
    Path sampleDir = util.getSampleDirForExerciseType(EXERCISE_TYPE);
    if(!Files.exists(sampleDir)) {
      try {
        Files.createDirectories(sampleDir);
      } catch (IOException e) {
        theLogger.error("Could not create directory for xml samples!", e);
      }
    }
    
    List<XmlExercise> exercises = XmlExercise.finder.all();
    if(exercises.isEmpty())
      theLogger.error("\t- No exercises found for Xml!");
    else
      for(XmlExercise exercise: exercises)
        checkOrCreateSampleFile(exercise);
  }
  
  private void checkOrCreateSampleFile(XmlExercise exercise) {
    Path sampleFile = Paths.get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString()
        + exercise.getReferenceFileEnding());
    if(Files.exists(sampleFile))
      return;
    
    theLogger.warn("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile
        + "\" existiert nicht! Versuche, Datei zu erstellen...");
    
    Path providedFile = Paths.get(BASE_DIR, exercise.referenceFileName + exercise.getReferenceFileEnding());
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
  
}
