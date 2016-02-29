package controllers.core;

import static controllers.core.Util.getSolDirForUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;

import model.user.Administrator;
import model.user.Student;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.login;

public class UserControl extends Controller {
  
  public static final String SESSION_ID_FIELD = "id";
  
  public static User getUser() {
    Http.Session session = Http.Context.current().session();
    return Student.find.byId(session.get(SESSION_ID_FIELD));
  }
  
  public Result authenticate() {
    Map<String, String[]> formValues = request().body().asFormUrlEncoded();
    
    String userName = formValues.get("name")[0];
    String passwort = formValues.get("passwort")[0];
    
    // TODO: schöner...
    User user = null;
    if(userName.equals("Administrator")) {
      user = new Administrator();
      ((Administrator) user).name = "Administrator";
    } else
      user = findOrCreateStudent(userName, passwort);
    
    session().clear();
    session(UserControl.SESSION_ID_FIELD, user.getName());
    
    if(user.isAdmin())
      return redirect("/admin");
    else
      return redirect("/");
  }
  
  public Result directLogin(String name, String type, int id) {
    String passwort = "";
    Student student = findOrCreateStudent(name, passwort);
    session().clear();
    session(UserControl.SESSION_ID_FIELD, student.name);
    
    return redirect("/" + type + "/" + id);
  }
  
  private Student findOrCreateStudent(String userName, String passwort) {
    // TODO: Passwort!
    if(Student.find.byId(userName) == null) {
      Student newStudent = new Student();
      newStudent.name = userName;
      newStudent.save();
      Path solutionDirectory = getSolDirForUser(userName);
      if(!Files.exists(solutionDirectory, LinkOption.NOFOLLOW_LINKS))
        try {
          Files.createDirectory(solutionDirectory);
        } catch (IOException e) {
        }
    }
    return Student.find.byId(userName);
  }
  
  public Result fromWuecampus(String type, int id, String name) {
    if(name.isEmpty())
      return redirect("/login");
    String passwort = "";
    Student student = findOrCreateStudent(name, passwort);
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
  
  public Result test() {
    return ok("TEST");
  }
  
}
