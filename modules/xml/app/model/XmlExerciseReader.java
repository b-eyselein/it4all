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
  
  public XmlExerciseReader() {
    super("xml");
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
    
    String filename = exercise.referenceFileName + "." + exercise.getReferenceFileEnding();
    
    Path providedFile = Paths.get(baseDirForExType.toString(), filename).toAbsolutePath();
    Path targetPath = Paths.get(baseTargetDir.toString(), filename).toAbsolutePath();
    
    if(!providedFile.toFile().exists()) {
      return "Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...";
    }
    
    try {
      Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
      return "Die Lösungsdatei wurde erstellt.";
    } catch (IOException e) {
      Logger.error("Fehler bei Erstellen von Musterlösung " + targetPath, e);
      return "Die Lösungsdatei konnte nicht erstellt werden!";
    }
  }
  
  @Override
  protected XmlExercise readExercise(JsonNode node) {
    int id = node.get(StringConsts.ID_NAME).asInt();
    
    XmlExercise exercise = XmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new XmlExercise(id);
    
    exercise.id = id;
    exercise.title = node.get(StringConsts.TITLE_NAME).asText();
    exercise.author = node.get(StringConsts.AUTHOR_NAME).asText();
    exercise.exerciseType = XmlExType.valueOf(node.get("exerciseType").asText());
    exercise.text = readTextArray(node.get(StringConsts.TEXT_NAME));
    exercise.referenceFileName = node.get("referenceFileName").asText();
    exercise.fixedStart = String.join("\n", JsonWrapper.parseJsonArrayNode(node.get("fixedStart")));
    
    return exercise;
  }
  
}
