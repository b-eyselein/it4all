package model.creation;

import model.exercise.CreationResult;
import model.exercise.CreationResultType;
import model.exercise.SqlExercise;

public class ExerciseCreationResult extends CreationResult<SqlExercise> {
  
  public ExerciseCreationResult(CreationResultType theResultType, String theMessage, SqlExercise theExercise) {
    super(theResultType, theExercise, theMessage);
  }
  
  @Override
  public String getAsHtml() {
    // TODO Auto-generated method stub
    String ret = "<tr class=\"" + getBSClass() + "\">";
    ret += "<td>" + resultType + "</td>";
    switch(resultType) {
    case FAILURE:
      ret += "<td>" + "X" + "</td>";
      ret += "<td colspan=\"3\"><p>Es gab einen Fehler beim Einlesen dieser Aufgabe: " + message + "</p></td>";
      break;
    case NOT_UPDATED:
    case TO_UPDATE:
    case NEW:
      ret += "<td>" + created.key.id + "</td>";
      ret += "<td>" + created.querytype + "</td>";
      ret += "<td><input class=\"form-control\" value=\"" + created.text + "\"</td>";
      ret += "<td>";
      for(String sampleSol: created.getSampleSolution())
        ret += "<p><input class=\"form-control\" value=\"" + sampleSol + "\"></p>";
      ret += "</p></td>";
      break;
    }
    ret += "</div>";
    return ret;
  }
  
}
