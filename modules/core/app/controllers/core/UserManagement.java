package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;

import model.Util;
import model.user.User;
import play.Environment;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.login;

public class UserManagement extends Controller {

  public static final String SESSION_ID_FIELD = "id";

  private Util util;
  private Environment env;

  @Inject
  public UserManagement(Util theUtil, Environment theEnv) {
    util = theUtil;
    env = theEnv;
  }

  public static User getCurrentUser() {
    Http.Session session = Http.Context.current().session();
    if(session == null || session.get(SESSION_ID_FIELD) == null || session.get(SESSION_ID_FIELD).isEmpty())
      throw new IllegalArgumentException("No user name was given!");
    return User.finder.byId(session.get(SESSION_ID_FIELD));
  }

  public Result authenticate() {
    if(env.isProd())
      return redirect(controllers.core.routes.UserManagement.login());
    
    Map<String, String[]> formValues = request().body().asFormUrlEncoded();

    String userName = formValues.get("name")[0];
    String passwort = formValues.get("passwort")[0];

    User user = findOrCreateStudent(userName, passwort);

    session().clear();
    session(UserManagement.SESSION_ID_FIELD, user.name);

    return redirect(controllers.routes.Application.index());
  }

  public Result directLogin(String name, String type, int id) {
    String passwort = "";
    User student = findOrCreateStudent(name, passwort);
    session().clear();
    session(UserManagement.SESSION_ID_FIELD, student.name);

    return redirect("/" + type + "/" + id);
  }

  public Result fromWuecampus(String name) {
    if(name.isEmpty())
      return redirect(controllers.core.routes.UserManagement.login());
    
    String passwort = "";
    User student = findOrCreateStudent(name, passwort);
    session().clear();
    session(UserManagement.SESSION_ID_FIELD, student.name);

    return redirect(controllers.routes.Application.index());
  }

  public Result login() {
    return ok(login.render());
  }

  public Result logout() {
    session().clear();
    return ok(login.render());
  }

  private User findOrCreateStudent(String userName, String passwort) {
    // TODO: Passwort!
    if(User.finder.byId(userName) == null) {
      User newStudent = new User();
      newStudent.name = userName;
      newStudent.save();
      Path solutionDirectory = util.getSolDirForUser(newStudent);
      if(!Files.exists(solutionDirectory, LinkOption.NOFOLLOW_LINKS))
        try {
          Files.createDirectories(solutionDirectory);
        } catch (IOException e) {
          Logger.error("Could not create solution directory for user " + userName, e);
        }
    }
    return User.finder.byId(userName);
  }

}
