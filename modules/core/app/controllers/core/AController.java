package controllers.core;

import model.user.User;
import play.mvc.Controller;
import play.mvc.Http;

public abstract class AController extends Controller {

  protected static final String SESSION_ID_FIELD = "id";
  
  protected static User getUser() {
    return User.finder.byId(getUsername());
  }

  protected static String getUsername() {
    Http.Session session = Http.Context.current().session();

    if(session == null || session.get(SESSION_ID_FIELD) == null || session.get(SESSION_ID_FIELD).isEmpty())
      throw new IllegalArgumentException("No user name was given!");

    return session.get(SESSION_ID_FIELD);
  }
  
}
