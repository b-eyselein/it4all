package model.testdata;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import model.ProgrammingUser;

@Entity
public class CommitedTestData extends ITestData {

  public static final Finder<CommitedTestDataKey, CommitedTestData> finder = new Finder<>(CommitedTestData.class);

  @EmbeddedId
  public CommitedTestDataKey key;

  @ManyToOne
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  @JsonBackReference
  public ProgrammingUser user;

  public CommitedTestData(CommitedTestDataKey theKey) {
    key = theKey;
  }

  public CommitedTestData(String theUserName, int theExerciseId, int theTestId, String theInputs, String theOutput) {
    this(new CommitedTestDataKey(theUserName, theExerciseId, theTestId));
    inputs = theInputs;
    output = theOutput;
  }

  @Override
  public String toString() {
    return key.exerciseId + ", " + key.testId + " :: " + inputs + " --> " + output;
  }

}
