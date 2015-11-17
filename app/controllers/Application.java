package controllers;

import static controllers.Util.getSolDirForUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;

import model.ExcelExercise;
import model.Exercise;
import model.user.Administrator;
import model.user.Student;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.login;

public class Application extends Controller {
  
  public static final String SESSION_ID_FIELD = "id";
  
  public Result index() {
    if(session(SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student student = Student.find.byId(session(SESSION_ID_FIELD));
    if(student == null) {
      session().clear();
      return redirect("/login");
    }
    return ok(index.render(student, Exercise.finder.all(), ExcelExercise.finder.all()));
  }
  
  public Result directLogin(String name, String type, int id) {
    String passwort = "";
    Student student = findOrCreateStudent(name, passwort);
    session().clear();
    session(SESSION_ID_FIELD, student.name);
    
    return redirect("/" + type + "/" + id);
  }
  
  public Result fromWuecampus(String type, int id, String name) {
    if(name.isEmpty())
      return redirect("/login");
    String passwort = "";
    Student student = findOrCreateStudent(name, passwort);
    session().clear();
    session(SESSION_ID_FIELD, student.name);
    
    return redirect("/" + type + "/" + id);
  }
  
  public Result login() {
    return ok(login.render());
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
    session(SESSION_ID_FIELD, user.getName());
    
    if(user.isAdmin())
      return redirect("/admin");
    else
      return redirect("/");
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
  
  public Result logout() {
    session().clear();
    return ok(login.render());
  }
  
}
