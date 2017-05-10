package model;

import model.user.Role;
import model.user.User;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

public class AdminSecured extends Security.Authenticator {
  
  @Override
  public String getUsername(Context ctx) {
    String userName = ctx.session().get(StringConsts.SESSION_ID_FIELD);
    
    if(userName == null) {
      ctx.session().clear();
      return null;
    }
    
    User user = User.finder.byId(userName);
    
    if(user != null && user.role == Role.ADMIN)
      return userName;
    
    return null;
  }
  
  @Override
  public Result onUnauthorized(Context ctx) {
    return redirect(controllers.routes.Application.index());
  }
  
}
