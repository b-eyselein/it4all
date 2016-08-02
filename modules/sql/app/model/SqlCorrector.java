package model;

import java.util.List;

import model.correctionResult.SqlCorrectionResult;
import model.exercise.SqlExercise;
import model.queryCorrectors.QueryCorrector;
import model.user.User;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public class SqlCorrector {
  
  public static SqlCorrectionResult correct(Database database, User user, String userStatement, SqlExercise exercise) {
    QueryCorrector<? extends Statement, ?> corrector = exercise.getCorrector();
    String sampleStatement = findBestFittingSample(userStatement, exercise.getSampleSolution());
    return corrector.correct(database, userStatement, sampleStatement, exercise);
  }
  
  private static String findBestFittingSample(String userStatement, List<String> samples) {
    String bestFitting = null;
    int bestDistance = Integer.MAX_VALUE;
    
    for(String sample: samples) {
      int newDistance = Levenshtein.levenshteinDistance(sample, userStatement);
      if(newDistance < bestDistance) {
        bestFitting = sample;
        bestDistance = newDistance;
      }
    }
    return bestFitting;
  }
}
