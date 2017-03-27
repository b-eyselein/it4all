package model;

import java.util.List;

import model.correctionresult.SqlResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.querycorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public final class SqlCorrector {
  
  private SqlCorrector() {
    
  }
  
  public static SqlResult correct(final Database database, final String userStatement, final SqlExercise exercise,
      final FeedbackLevel feedbackLevel) {
    final QueryCorrector<? extends Statement, ?> corrector = exercise.getCorrector();
    
    final String sampleStatement = findBestFittingSample(userStatement, exercise.getSampleSolutions());
    
    return corrector.correct(database, userStatement, sampleStatement, exercise, feedbackLevel);
  }
  
  private static String findBestFittingSample(final String userStatement, final List<String> samples) {
    String bestFitting = null;
    int bestDistance = Integer.MAX_VALUE;
    
    for(final String sample: samples) {
      final int newDistance = Levenshtein.levenshteinDistance(sample, userStatement);
      if(newDistance < bestDistance) {
        bestFitting = sample;
        bestDistance = newDistance;
      }
    }
    return bestFitting;
  }
}
