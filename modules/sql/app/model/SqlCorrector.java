package model;

import java.util.List;

import model.correctionresult.SqlResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlSample;
import model.querycorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public final class SqlCorrector {

  private SqlCorrector() {

  }

  public static SqlResult correct(final Database database, final String userStatement, final SqlExercise exercise,
      final FeedbackLevel feedbackLevel) {
    final QueryCorrector<? extends Statement, ?> corrector = exercise.getCorrector();

    final SqlSample sampleStatement = findBestFittingSample(userStatement, exercise.samples);

    return corrector.correct(database, userStatement, sampleStatement, exercise, feedbackLevel);
  }

  private static SqlSample findBestFittingSample(final String userStatement, final List<SqlSample> samples) {
    return samples.parallelStream().min((samp1, samp2) -> {
      int dist1 = Levenshtein.levenshteinDistance(samp1.sample, userStatement);
      int dist2 = Levenshtein.levenshteinDistance(samp2.sample, userStatement);
      return dist1 - dist2;
    }).orElse(samples.get(0));

  }
}
