package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.exercisereading.ExerciseReadingException;
import play.Logger;

public class UmlExerciseReader extends ExerciseReader<UmlExercise> {

  public UmlExerciseReader() {
    super("uml");
  }

  private String readFile(String fileName) throws ExerciseReadingException {
    Path fileToRead = Paths.get(BASE_DIR, exerciseType, fileName);

    if(!fileToRead.toFile().exists())
      throw new ExerciseReadingException("File " + fileToRead + " does not exist!");

    try {
      return String.join("\n", Files.readAllLines(fileToRead));
    } catch (IOException e) {
      throw new ExerciseReadingException("Error while reading file " + fileToRead.toString(), e);
    }
  }

  @Override
  protected UmlExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get("id");
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode rawtextNode = exerciseNode.get("rawtext");
    JsonNode mappingsNode = exerciseNode.get("mappings");
    JsonNode ingoreNode = exerciseNode.get("ignore");

    int id = idNode.asInt();
    UmlExercise exercise = UmlExercise.finder.byId(id);
    if(exercise == null)
      exercise = new UmlExercise(id);

    String rawText = "";
    try {
      rawText = readFile(rawtextNode.asText());
    } catch (ExerciseReadingException e) {
      Logger.error("Error while reading exercise:", e);
    }

    Path baseFormsPath = Paths.get(BASE_DIR, exerciseType, mappingsNode.asText());
    
    exercise.classSelText = UmlExerciseTextParser.parseTextForClassSel(rawText, baseFormsPath,
        JsonWrapper.parseJsonArrayNode(ingoreNode));
    exercise.diagDrawHelpText = rawText;
    exercise.diagDrawText = rawText;
    exercise.text = rawText;
    exercise.title = titleNode.asText();

    return exercise;
  }

}
