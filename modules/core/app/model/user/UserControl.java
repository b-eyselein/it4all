package model.user;

import play.mvc.Http;

public class UserControl {
  
  public static final String SESSION_ID_FIELD = "id";
  
  public static User getUser() {
    Http.Session session = Http.Context.current().session();
    return Student.find.byId(session.get(SESSION_ID_FIELD));
  }
  
}
