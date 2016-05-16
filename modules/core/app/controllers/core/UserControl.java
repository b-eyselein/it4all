package controllers.core;

import static controllers.core.Util.getSolDirForUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;

import model.user.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.login;

public class UserControl extends Controller {
  
  public static final String SESSION_ID_FIELD = "id";

  public static User getCurrentUser() {
    Http.Session session = Http.Context.current().session();
    if(session.get(SESSION_ID_FIELD).equals(""))
      throw new IllegalArgumentException("problem!");
    return User.finder.byId(session.get(SESSION_ID_FIELD));
  }

  public Result authenticate() {
    Map<String, String[]> formValues = request().body().asFormUrlEncoded();

    String userName = formValues.get("name")[0];
    String passwort = formValues.get("passwort")[0];

    User user = findOrCreateStudent(userName, passwort);

    session().clear();
    session(UserControl.SESSION_ID_FIELD, user.name);

    return redirect(controllers.routes.Application.index());
  }

  public Result directLogin(String name, String type, int id) {
    String passwort = "";
    User student = findOrCreateStudent(name, passwort);
    session().clear();
    session(UserControl.SESSION_ID_FIELD, student.name);

    return redirect("/" + type + "/" + id);
  }

  public Result fromWuecampus(String type, int id, String name) {
    if(name.isEmpty())
      return redirect("/login");
    String passwort = "";
    User student = findOrCreateStudent(name, passwort);
    session().clear();
    session(UserControl.SESSION_ID_FIELD, student.name);

    return redirect("/" + type + "/" + id);
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
      Path solutionDirectory = getSolDirForUser(userName);
      if(!Files.exists(solutionDirectory, LinkOption.NOFOLLOW_LINKS))
        try {
          Files.createDirectory(solutionDirectory);
        } catch (IOException e) {
        }
    }
    return User.finder.byId(userName);
  }

}
