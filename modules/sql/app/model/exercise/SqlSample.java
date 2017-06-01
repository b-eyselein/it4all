package model.exercise;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.Splitter;

@Entity
public class SqlSample extends Model {

  protected static final Splitter NEWLINE_SPLITTER = Splitter.on("\n");

  public static final Finder<SqlSampleKey, SqlSample> finder = new Finder<>(SqlSample.class);

  @EmbeddedId
  public SqlSampleKey key;

  public String sample;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "scenario_id", referencedColumnName = "scenario_id", insertable = false, updatable = false),
      @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false)})
  @JsonBackReference
  public SqlExercise exercise;

  public SqlSample(SqlSampleKey theKey) {
    key = theKey;
  }

  public List<String> getSample() {
    return NEWLINE_SPLITTER.splitToList(sample);
  }

}