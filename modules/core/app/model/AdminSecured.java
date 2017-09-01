package model;

import model.user.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class AdminSecured extends Security.Authenticator {

  @Override
  public String getUsername(Context ctx) {
    String userName = ctx.session().get(StringConsts.SESSION_ID_FIELD);

    if(userName == null) {
      ctx.session().clear();
      return null;
    }

    User user = User.finder.byId(userName);

    return user != null && user.isAdmin() ? userName : null;
  }

  @Override
  public Result onUnauthorized(Context ctx) {
    return redirect(controllers.routes.Application.index());
  }

}
