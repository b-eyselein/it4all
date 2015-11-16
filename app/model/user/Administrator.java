package model.user;

public class Administrator implements User {
  
  // FIXME: change name and password!!!!
  public final String name = "administrator";
  private String password = "test";
  
  @Override
  public String getName() {
    return name;
  }
  
}
