package model.testdata;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.ebean.Finder;
import model.ApprovalState;
import model.ProgrammingUser;
import model.user.User;

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

  public static List<CommitedTestData> forUserAndExercise(User user, int exerciseId) {
    return finder.all().stream().filter(td -> td.user.equals(user) && td.key.exerciseId == exerciseId)
        .collect(Collectors.toList());
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
