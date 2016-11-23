package model;

import java.util.List;

import model.correctionresult.SqlCorrectionResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.querycorrectors.QueryCorrector;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public class SqlCorrector {

  private SqlCorrector() {

  }

  public static CompleteResult correct(Database database, String userStatement, SqlExercise exercise,
      FeedbackLevel feedbackLevel) {
    QueryCorrector<? extends Statement, ?> corrector = exercise.getCorrector();

    String sampleStatement = findBestFittingSample(userStatement, exercise.getSampleSolutions());

    List<EvaluationResult> results = corrector.correct(database, userStatement, sampleStatement, exercise,
        feedbackLevel);

    return new SqlCorrectionResult(userStatement, results);
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
