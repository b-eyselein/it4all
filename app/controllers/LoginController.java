package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import controllers.core.BaseController;
import model.StringConsts;
import model.user.Course;
import model.user.User;
import play.Environment;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class LoginController extends BaseController {

  private Environment env;

  @Inject
  public LoginController(FormFactory theFactory, Environment theEnv) {
    super(theFactory);
    env = theEnv;
  }

  public Result authenticate() {
    if(env.isProd())
      // Disable login in prod mode
      return redirect(routes.LoginController.login());

    DynamicForm form = factory.form().bindFromRequest();
    String userName = form.get(StringConsts.NAME_NAME);
    String passwort = form.get(StringConsts.PW_NAME);

    User user = findOrCreateStudent(userName, passwort);

    session().clear();
    session(StringConsts.SESSION_ID_FIELD, user.name);

    return redirect(controllers.routes.Application.index());
  }

  public Result fromWuecampus(String userName, int courseId, String courseName) {
    if(userName.isEmpty())
      return redirect(routes.LoginController.login());

    String passwort = "";
    User student = findOrCreateStudent(userName, passwort);

    if(Course.finder.byId(courseId) == null && courseId != -1) {
      // Create course
      Course course = new Course(courseId);
      course.name = courseName;
      course.save();
    }

    session().clear();
    session(StringConsts.SESSION_ID_FIELD, student.name);

    return redirect(controllers.routes.Application.index());
  }

  public Result login() {
    return ok(views.html.login.render());
  }

  public Result logout() {
    session().clear();
    return redirect(routes.LoginController.login());
  }

  private User findOrCreateStudent(String userName, String passwort) {
    // TODO: Passwort!
    User user = User.finder.byId(userName);

    if(user != null)
      return user;

    user = new User();
    user.name = userName;
    user.save();

    Path solutionDirectory = getSolDirForUser(userName);
    if(solutionDirectory.toFile().exists())
      return user;

    try {
      Files.createDirectories(solutionDirectory);
    } catch (IOException e) {
      Logger.error("Could not create solution directory for user " + userName, e);
    }

    return user;
  }

}
