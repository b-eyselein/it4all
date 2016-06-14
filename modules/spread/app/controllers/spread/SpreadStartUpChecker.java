package controllers.spread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import model.Util;
import model.spread.ExcelExercise;
import play.Logger;

public class SpreadStartUpChecker {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  private static final String EXERCISE_TYPE = "spread";
  private static final List<String> FILE_ENDINGS = Arrays.asList("xlsx", "ods");

  private Util util;

  @Inject
  public SpreadStartUpChecker(Util theUtil) {
    util = theUtil;
    performStartUpCheck();
  }

  public void performStartUpCheck() {
    List<ExcelExercise> exercises = ExcelExercise.finder.all();
    if(exercises.size() == 0)
      theLogger.error("\t- No exercises found for Xml!");
    else
      for(ExcelExercise exercise: exercises)
        for(String fileEnding: FILE_ENDINGS)
          checkOrCreateSampleFile(exercise, fileEnding);
  }

  private void checkOrCreateSampleFile(ExcelExercise exercise, String fileEnding) {
    Path sampleFileDirectory = util.getSampleDirForExercise(EXERCISE_TYPE);
    if(!Files.exists(sampleFileDirectory)) {
      try {
        Files.createDirectories(sampleFileDirectory);
      } catch (IOException e) {
        theLogger.error("Konnte das Verzeichnis für die Musterlösungen zu Aufgabe " + exercise.id + " nicht erstellen!",
            e);
      }
    }

    Path sampleFile = util.getSampleFileForExercise(EXERCISE_TYPE, exercise.fileName + "_Muster." + fileEnding);
    if(Files.exists(sampleFile))
      return;
    
    theLogger.warn("Die Lösungsdatei für Xml-Aufgabe " + exercise.id + " \"" + sampleFile
        + "\" existiert nicht! Versuche, Datei zu erstellen...");

    Path providedFile = Paths.get("modules/" + EXERCISE_TYPE + "/conf/resources",
        exercise.fileName + "_Muster." + fileEnding);
    if(!Files.exists(providedFile)) {
      theLogger.error("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert in " + providedFile);
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
