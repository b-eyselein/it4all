package controllers.xml;

import java.nio.file.Files;
import java.nio.file.Path;
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
      for(XmlExercise exercise: exercises) {
        Path sampleFile = util.getSampleFileForExerciseAndType("xml", exercise.referenceFileName);
        if(!Files.exists(sampleFile))
          theLogger.error("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile + "\" existiert nicht!");
      }
  }

}
