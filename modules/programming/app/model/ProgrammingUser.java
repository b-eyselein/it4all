package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;
import io.ebean.Model;
import model.testdata.CommitedTestData;

@Entity
public class ProgrammingUser extends Model {

  public static final Finder<String, ProgrammingUser> finder = new Finder<>(ProgrammingUser.class);

  @Id
  public String name;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  @JsonManagedReference
  public List<CommitedTestData> commitedTestData;

  public ProgrammingUser(String theName) {
    name = theName;
  }

}
