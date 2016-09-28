package model.creation;

import java.util.List;

import model.exercise.CreationResult;
import model.exercise.CreationResultType;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;

public class ExerciseCreationResult extends CreationResult<SqlExercise> {

  private static String querytypeSelect(int exerciseId, SqlExerciseType querytype) {
    String ret = "<select class=\"form-control\" name=\"ex" + exerciseId + "_type\">";
    for(SqlExerciseType type: SqlExerciseType.values())
      ret += "<option" + (type == querytype ? " selected" : "") + ">" + type.toString() + "</option>";
    ret += "</select>";
    return ret;
  }

  private int id;

  // TODO: use members instead of complete class?
  // private SqlExerciseType type;
  // private String text;
  // private List<String> samples;
  // private String validation;

  public ExerciseCreationResult(CreationResultType theResultType, String theMessage, int theExericseId,
      SqlExercise theExercise) {
    super(theResultType, theExercise, theMessage);
    id = theExericseId;
  }

  @Override
  public String getAsHtml() {
    String ret = "<tr class=\"" + getBSClass() + "\">\n";
    ret += "<td>" + resultType + "</td>\n";
    ret += "<td>" + id + "</td>";

    if(resultType == CreationResultType.FAILURE) {
      ret += "<td colspan=\"4\"><p>Es gab einen Fehler beim Einlesen dieser Aufgabe: " + message + "</p></td>";
    } else {
      // Querytype
      ret += "<td>" + querytypeSelect(created.key.id, created.querytype) + "</td>\n";

      // Exercise text
      ret += "<td><textarea class=\"form-control\" rows=\"3\" name=\"ex" + created.key.id + "_text\">" + created.text
          + "</textarea></td>\n";

      // Sample solutions
      ret += "<td>\n";
      List<String> samples = created.getSampleSolutions();
      for(int i = 0; i < samples.size(); i++)
        ret += "<p><input class=\"form-control\" name=\"ex" + created.key.id + "_sample" + i + "\" value=\""
            + samples.get(i) + "\"></p>\n";
      ret += "</p></td>";

      // Validation
      ret += "<td><input class=\"form-control\" value=\"--TODO--\"></td>";
    }

    ret += "</div>";
    return ret;
  }

}
