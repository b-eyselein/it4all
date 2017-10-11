package controllers;

import controllers.core.BaseController;
import model.AdminSecured;
import model.feedback.Feedback;
import model.feedback.FeedbackResult;
import model.feedback.FeedbackResult$;
import model.user.Role;
import model.user.User;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

import javax.inject.Inject;
import java.util.List;

@Authenticated(AdminSecured.class) public class AdminController extends BaseController {

  @Inject public AdminController(FormFactory f) {
    super(f);
  }

  public Result changeRole(String username) {
    if(getUser().stdRole != Role.SUPERADMIN)
      return forbidden("You do not have sufficient privileges to change roles!");

    String newrole = factory().form().bindFromRequest().get("newrole");

    User userToChange = User.finder.byId(username);
    userToChange.stdRole = Role.valueOf(newrole);
    userToChange.save();

    return ok(Json.toJson(userToChange));
  }

  public Result evaluation() {
    List<FeedbackResult> results = FeedbackResult$.MODULE$.evaluate(Feedback.finder.all());
    return ok(views.html.evaluation.stats.render(getUser(), results));
  }

  public Result index() {
    return ok(views.html.admin.adminPage.render(getUser()));
  }

  public Result users() {
    return ok(views.html.admin.users.render(getUser(), User.finder.all()));
  }

}
