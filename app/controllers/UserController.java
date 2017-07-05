package controllers;

import javax.inject.Inject;

import controllers.core.BaseController;
import model.Secured;
import model.StringConsts;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class UserController extends BaseController {
  
  @Inject
  public UserController(FormFactory theFactory) {
    super(theFactory);
  }
  
  public Result index() {
    return ok(views.html.user.user.render("User", getUser()));
  }
  
  public Result preferences() {
    return ok(views.html.user.preferences.render("Präferenzen", getUser()));
  }
  
  public Result saveOptions() {
    DynamicForm form = factory.form().bindFromRequest();
    
    User user = User.finder.byId(session().get(StringConsts.SESSION_ID_FIELD));
    user.setTodo(User.SHOW_HIDE_AGGREGATE.valueOf(form.get("posTests")));
    user.save();
    
    // FIXME: tell user that settings habe been saved!
    
    return redirect(controllers.routes.UserController.preferences());
  }
  
}
