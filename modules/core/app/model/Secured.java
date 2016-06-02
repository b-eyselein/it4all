package model;

import model.user.User;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

public class Secured extends Security.Authenticator {
  
  public static final String SESSION_ID_FIELD = "id";

  @Override
  public String getUsername(Context ctx) {
    String userName = ctx.session().get(SESSION_ID_FIELD);
    if(userName == null || User.finder.byId(userName) == null)
      ctx.session().clear();
    
    return userName;
  }

  @Override
  public Result onUnauthorized(Context ctx) {
    return redirect(controllers.core.routes.UserManagement.login());
  }

}
