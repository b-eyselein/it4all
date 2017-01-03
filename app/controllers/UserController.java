package controllers;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.user;
import views.html.user.preferences;

@Security.Authenticated(Secured.class)
public class UserController extends Controller {
  
  private static final String SESSION_ID_FIELD = "id";
  private FormFactory factory;
  
  @Inject
  public UserController(FormFactory theFactory) {
    factory = theFactory;
  }
  
  public Result index() {
    return ok(user.render("User", UserManagement.getCurrentUser()));
  }
  
  public Result preferences() {
    return ok(preferences.render("Präferenzen", UserManagement.getCurrentUser()));
  }
  
  public Result saveOptions() {
    DynamicForm form = factory.form().bindFromRequest();
    
    User user = User.finder.byId(session().get(SESSION_ID_FIELD));
    user.setTodo(User.SHOW_HIDE_AGGREGATE.valueOf(form.get("posTests")));
    user.save();
    
    // FIXME: tell user that settings habe been saved!
    
    return redirect(controllers.routes.UserController.preferences());
  }
  
}
