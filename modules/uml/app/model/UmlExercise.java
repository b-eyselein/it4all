package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import model.exercise.Exercise;

public class UmlExercise extends Exercise {

  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");

  private static List<UmlExercise> exercises = Arrays.asList(
    // @formatter:off
    new UmlExercise(1,
        "Krankenhaus",
        "Im folgenden Übungsszenario sollen Sie ein Klassendiagramm in UML mit dem Thema Krankenhaus erstellen!"),
    new UmlExercise(2,
        "Gärtner",
        "TODO")
    // @formatter:on
  );

  private String classSelText;
  private String diagDrawHelpText;
  private String /* TODO: change type... */ musterSolution;

  public UmlExercise(int theId, String theRawText, String theTitle) {
    super(theId);
    title = theTitle;
    text = theRawText;
  }

  public static UmlExercise getExercise(int id) {
    for(UmlExercise ex: exercises)
      if(ex.id == id)
        return ex;
    return null;
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
    // TODO: return classSelText;
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