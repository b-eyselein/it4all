package model.testdata;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import model.ApprovalState;
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

  @Enumerated(EnumType.STRING)
  public ApprovalState approvalState;

  public CommitedTestData(CommitedTestDataKey theKey) {
    key = theKey;
  }

  public CommitedTestData(CommitedTestDataKey theKey, String theInputs, String theOutput) {
    this(theKey);
    inputs = theInputs;
    output = theOutput;
  }

  @Override
  public int getId() {
    return key.testId;
  }

  @Override
  public String toString() {
    return key.exerciseId + ", " + key.testId + " :: " + inputs + " --> " + output;
  }

}
