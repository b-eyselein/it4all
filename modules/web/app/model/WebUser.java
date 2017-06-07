package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class WebUser extends Model {

  public static final Finder<String, WebUser> finder = new Finder<>(WebUser.class);

  @Id
  public String name;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  public List<WebSolution> solutions;

  public WebUser(String theName) {
    name = theName;
  }

}
