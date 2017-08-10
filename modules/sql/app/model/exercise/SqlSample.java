package model.exercise;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class SqlSample extends Model {

  protected static final Splitter NEWLINE_SPLITTER = Splitter.on("\n");

  public static final Finder<SqlSampleKey, SqlSample> finder = new Finder<>(SqlSample.class);

  @EmbeddedId
  public SqlSampleKey key;
  
  @JsonProperty(required = true)
  public String sample;

  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public SqlExercise exercise;

  public SqlSample(SqlSampleKey theKey) {
    key = theKey;
  }

  public List<String> getSample() {
    return NEWLINE_SPLITTER.splitToList(sample);
  }

}
