package model.exercise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

  public static final Finder<Integer, SqlExercise> finder = new Finder<>(SqlExercise.class);

  @Column(columnDefinition = "text")
  public String samples;

  @Enumerated(EnumType.STRING)
  public SqlExerciseType exercisetype;

  @ManyToOne
  @JoinColumn(name = "scenario_name")
  public SqlScenario scenario;

  public String validation; // NOSONAR

  public String tags; // NOSONAR

  public String hint; // NOSONAR

  public SqlExercise(int theId) {
    super(theId);
  }

  public Html getBadges() {
    return new Html(getTags().stream().map(SqlTag::getButtonContent).collect(Collectors.joining()));
  }

  public QueryCorrector<? extends Statement, ?> getCorrector() {
    switch(exercisetype) {
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

  public List<String> getSampleSolutions() {
    return Arrays.asList(samples.split("#"));
  }

  public List<SqlTag> getTags() {
    if(tags.isEmpty())
      return Collections.emptyList();

    return Arrays.stream(tags.split(SAMPLE_JOIN_CHAR)).map(SqlTag::valueOf).collect(Collectors.toList());
  }

  public Html renderSampleSolutions() {
    return new Html(getSampleSolutions().stream().collect(Collectors
        .joining("</pre></div><div class=\"col-md-6\"><pre>", "<div class=\"col-md-6\"><pre>", "</pre></div>")));
  }

}
