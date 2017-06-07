package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class SqlUser extends Model {

  public static final Finder<String, SqlUser> finder = new Finder<>(SqlUser.class);

  @Id
  public String name;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  public List<SqlSolution> solutions;

  public SqlUser(String theName) {
    name = theName;
  }

}
