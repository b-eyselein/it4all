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
import play.Logger;

public class XmlStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  
  private Util util;
  
  @Inject
  public XmlStartUpChecker(Util theUtil) {
    util = theUtil;
    performStartUpCheck();
  }
  
  public void performStartUpCheck() {
    List<XmlExercise> exercises = XmlExercise.finder.all();
    if(exercises.size() == 0)
      theLogger.error("\t- No exercises found for Xml!");
    else
      for(XmlExercise exercise: exercises)
        checkOrCreateSampleFile(exercise);
  }
  
  private void checkOrCreateSampleFile(XmlExercise exercise) {
    Path sampleFile = util.getSampleFileForExerciseAndType("xml", exercise.referenceFileName);
    if(Files.exists(sampleFile))
      return;
    
    theLogger.warn("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile
        + "\" existiert nicht! Versuche, Datei zu erstellen...");
    
    Path providedFile = Paths.get("modules/xml/conf/resources", exercise.referenceFileName);
    if(Files.exists(providedFile))
      try {
        Files.copy(providedFile, sampleFile, StandardCopyOption.REPLACE_EXISTING);
        theLogger.info("Die Lösungsdatei wurde erstellt.");
      } catch (IOException e) {
        theLogger.error("Die Lösungsdatei konnte nicht erstellt werden!", e);
      }
    else
      theLogger.error("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...");
  }
  
}
