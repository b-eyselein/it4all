package model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import model.programming.TestData;
import model.programming.TestDataKey;

@Entity
public class PythonTestData extends TestData {
  
  public static final Finder<TestDataKey, PythonTestData> finder = new Finder<>(PythonTestData.class);
  
  @ManyToOne
  @JoinColumn(name = "exercise_id", updatable = false, insertable = false)
  @JsonBackReference
  public PythonExercise exercise;
  
  public PythonTestData(TestDataKey theKey) {
    super(theKey);
  }
  
}
