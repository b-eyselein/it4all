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
public class ProgUser extends Model {

  public static final Finder<String, ProgUser> finder = new Finder<>(ProgUser.class);

  @Id
  public String name;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  @JsonManagedReference
  public List<CommitedTestData> commitedTestData;

  public ProgUser(String theName) {
    name = theName;
  }

}
