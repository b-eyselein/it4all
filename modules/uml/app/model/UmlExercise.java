package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {

  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");

  public UmlExercise(int theId) {
    super(theId);
    id = 1;
    text = "Im folgenden Übungsszenario sollen Sie ein Klassendiagramm in UML erstellen!";
    title = "Foto";
    // TODO Auto-generated constructor stub
  }

  public String getExerciseText() {
    try {
      return String.join("\n", Files.readAllLines(Paths.get(BASE_PATH.toString(), "dummyExerciseText.html")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      return "ERROR!!!";
    }
  }

  public String getExTextForClassSel() {
    try {
      return String.join("\n", Files.readAllLines(Paths.get(BASE_PATH.toString(), "dummyExTextForClassSel.html")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      return "ERROR!!!";
    }
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

}