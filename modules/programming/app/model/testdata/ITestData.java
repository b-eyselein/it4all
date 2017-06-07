package model.testdata;

import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.ebean.Model;
import model.ProgExercise;

@MappedSuperclass
public abstract class ITestData extends Model {

  public static final String VALUES_SPLIT_CHAR = "#";

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  @JsonBackReference
  public ProgExercise exercise;

  @Column(columnDefinition = "text")
  public String inputs;

  public String output;

  public abstract int getId();

  public List<String> getInput() {
    return Arrays.asList(inputs.split(VALUES_SPLIT_CHAR));
  }

}