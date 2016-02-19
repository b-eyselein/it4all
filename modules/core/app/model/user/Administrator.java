package model.user;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;

@Entity
public class Administrator implements User {
  
  @Id
  public String name;
  
  @Required
  public String password;

  @Override
  public String getName() {
    return name;
  }
  // FIXME: change name and password!!!!

  @Override
  public boolean isAdmin() {
    return true;
  }
  
}
