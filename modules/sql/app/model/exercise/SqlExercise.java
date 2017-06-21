package model.exercise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.Splitter;

import io.ebean.Finder;
import io.ebean.Model;
import model.SqlSolution;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.change.DeleteCorrector;
import model.querycorrectors.change.InsertCorrector;
import model.querycorrectors.change.UpdateCorrector;
import model.querycorrectors.create.CreateCorrector;
import model.querycorrectors.select.SelectCorrector;
import net.sf.jsqlparser.statement.Statement;

@Entity
public class SqlExercise extends Model {
  
  private static final Splitter SPLITTER = Splitter.fixedLength(100).omitEmptyStrings();
  
  public static final String SAMPLE_JOIN_CHAR = "#";
  
  public static final Finder<SqlExerciseKey, SqlExercise> finder = new Finder<>(SqlExercise.class);
  
  @EmbeddedId
  public SqlExerciseKey key;
  
  public String author;
  
  @Column(columnDefinition = "text")
  public String text;
  
  @Enumerated(EnumType.STRING)
  public SqlExerciseType exerciseType;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<SqlSample> samples;
  
  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<SqlSolution> solutions;
  
  @ManyToOne
  @JoinColumn(name = "scenario_id", insertable = false, updatable = false)
  @JsonBackReference
  public SqlScenario scenario;
  
  public String tags;
  
  public String hint;
  
  public SqlExercise(SqlExerciseKey theKey) {
    key = theKey;
  }
  
  @JsonIgnore
  public String getBadges() {
    return getTags().stream().map(SqlTag::getButtonContent).collect(Collectors.joining());
  }
  
  @JsonIgnore
  public QueryCorrector<? extends Statement, ?> getCorrector() {
    // FIXME: different...
    switch(exerciseType) {
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
  
  public List<SqlTag> getTags() {
    if(tags.isEmpty())
      return Collections.emptyList();
    
    return Arrays.stream(tags.split(SAMPLE_JOIN_CHAR)).map(SqlTag::valueOf).collect(Collectors.toList());
  }
  
  public List<String> getText() {
    return SPLITTER.splitToList(text);
  }
  
}
