package model;

import model.user.User;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

public class Secured extends Security.Authenticator {

  @Override
  public String getUsername(Context ctx) {
    String userName = ctx.session().get(StringConsts.SESSION_ID_FIELD);
    if(userName == null || User.finder.byId(userName) == null) {
      ctx.session().clear();
      return null;
    }

    return userName;
  }

  @Override
  public Result onUnauthorized(Context ctx) {
    return redirect(controllers.core.routes.UserManagement.login());
  }

}
