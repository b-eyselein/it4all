package controllers;

import model.user.Student;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

public class Secured extends Security.Authenticator {
  
  public static final String SESSION_ID_FIELD = "id";
  
  public String getUsername(Context ctx) {
    String userName = ctx.session().get(SESSION_ID_FIELD);
    if(Student.find.byId(userName) == null)
      ctx.session().clear();
    return userName;
  }
  
  public Result onUnauthorized(Context ctx) {
    return redirect(routes.Application.login());
  }
  
}
