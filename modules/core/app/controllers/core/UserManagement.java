package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;

import model.StringConsts;
import model.Util;
import model.user.User;
import play.Environment;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Result;
import views.html.login;

public class UserManagement extends AbstractController {
  
  private Environment env;
  
  @Inject
  public UserManagement(Util theUtil, FormFactory theFactory, Environment theEnv) {
    super(theUtil, theFactory);
    env = theEnv;
  }
  
  public Result authenticate() {
    if(env.isProd())
      return redirect(controllers.core.routes.UserManagement.login());
    
    Map<String, String[]> formValues = request().body().asFormUrlEncoded();
    
    String userName = formValues.get("name")[0];
    String passwort = formValues.get("passwort")[0];
    
    User user = findOrCreateStudent(userName, passwort);
    
    session().clear();
    session(StringConsts.SESSION_ID_FIELD, user.name);
    
    return redirect(controllers.routes.Application.index());
  }
  
  public Result directLogin(String name, String type, int id) {
    String passwort = "";
    User student = findOrCreateStudent(name, passwort);
    session().clear();
    session(StringConsts.SESSION_ID_FIELD, student.name);
    
    return redirect("/" + type + "/" + id);
  }
  
  public Result fromWuecampus(String name) {
    if(name.isEmpty())
      return redirect(controllers.core.routes.UserManagement.login());
    
    String passwort = "";
    User student = findOrCreateStudent(name, passwort);
    session().clear();
    session(StringConsts.SESSION_ID_FIELD, student.name);
    
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
      if(!solutionDirectory.toFile().exists())
        try {
          Files.createDirectories(solutionDirectory);
        } catch (IOException e) {
          Logger.error("Could not create solution directory for user " + userName, e);
        }
    }
    return User.finder.byId(userName);
  }
  
}
