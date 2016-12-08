package model.exercise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import model.querycorrectors.CreateCorrector;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.SelectCorrector;
import model.querycorrectors.update.DeleteCorrector;
import model.querycorrectors.update.InsertCorrector;
import model.querycorrectors.update.UpdateCorrector;
import net.sf.jsqlparser.statement.Statement;
import play.twirl.api.Html;

@Entity
public class SqlExercise extends Exercise {

  public static final String SAMPLE_JOIN_CHAR = "#";

  public static final Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);

  @EmbeddedId
  public SqlExerciseKey key;

  @Column(columnDefinition = "text")
  public String text;

  @Column(columnDefinition = "text")
  public String samples;

  @ManyToOne
  @JoinColumn(name = "scenario_name", insertable = false, updatable = false)
  public SqlScenario scenario;

  public String validation; // NOSONAR

  public String tags; // NOSONAR

  public String hint; // NOSONAR

  public SqlExercise(SqlExerciseKey theKey) {
    key = theKey;
  }

  public Html getBadges() {

    return new Html(getTags().stream().map(SqlTag::getButtonContent).collect(Collectors.joining()));
  }

  public QueryCorrector<? extends Statement, ?> getCorrector() {
    switch(key.exercisetype) {
    case CREATE:
      return new CreateCorrector();
    case DELETE:
      return new DeleteCorrector();
    case INSERT:
      return new InsertCorrector();
    case SELECT:
      return new SelectCorrector();
    case UPDATE:
      return new UpdateCorrector();
    default:
      return null;
    }
  }

  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  public List<String> getSampleSolutions() {
    return Arrays.asList(samples.split("#"));
  }

  public List<SqlTag> getTags() {
    if(tags.isEmpty())
      return Collections.emptyList();

    return Arrays.stream(tags.split(SAMPLE_JOIN_CHAR)).map(SqlTag::valueOf).collect(Collectors.toList());
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    return null;
  }

  public Html renderSampleSolutions() {
    return new Html(getSampleSolutions().stream().collect(Collectors
        .joining("</pre></div><div class=\"col-md-6\"><pre>", "<div class=\"col-md-6\"><pre>", "</pre></div>")));
  }

}
