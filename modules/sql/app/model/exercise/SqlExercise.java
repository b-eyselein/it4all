package model.exercise;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.ebean.Finder;
import model.SqlSolution;

@Entity
public class SqlExercise extends Exercise {

  public static final String SAMPLE_JOIN_CHAR = "#";

  public static final Finder<Integer, SqlExercise> finder = new Finder<>(SqlExercise.class);

  @Enumerated(EnumType.STRING)
  @JsonProperty(required = true)
  public SqlExerciseType exerciseType;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<SqlSample> samples;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
  @JsonIgnore
  public List<SqlSolution> solutions;

  @ManyToOne
  @JsonBackReference
  public SqlScenario scenario;

  @JsonIgnore
  public String tags;

  @JsonProperty(required = true)
  public String hint;

  public SqlExercise(int id) {
    super(id);
  }

  @Override
  public List<SqlTag> getTags() {
    if(tags.isEmpty())
      return Collections.emptyList();

    return Arrays.stream(tags.split(SAMPLE_JOIN_CHAR)).map(SqlTag::valueOf).collect(Collectors.toList());
  }

  @JsonProperty(value = "tags", required = true)
  public List<String> getTagsForJson() {
    return Arrays.stream(tags.split(SAMPLE_JOIN_CHAR)).collect(Collectors.toList());
  }

}
