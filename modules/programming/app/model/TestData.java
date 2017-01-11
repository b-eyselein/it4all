package model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class TestData extends Model implements ITestData {

  protected static final String VALUES_SPLIT_CHAR = "#";

  @EmbeddedId
  public TestDataKey key;

  @ManyToOne
  public ProgrammingExercise exercise;
  
  @Column(columnDefinition = "text")
  public String inputs;

  public String output; // NOSONAR

  public TestData(TestDataKey theKey) {
    key = theKey;
  }

  @Override
  public int getId() {
    return key.testId;
  }

  @Override
  public List<String> getInput() {
    return Arrays.asList(inputs.split(VALUES_SPLIT_CHAR));
  }

  @Override
  public String getOutput() {
    return output;
  }

}
