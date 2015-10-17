package controllers;

import static play.data.Form.form;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Student;
import play.data.Form;
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
    return ok(index.render(student));
  }
  
  public Result directlogin(int exercise, String snr) {
    Student student = findOrCreateStudent(snr);
    session().clear();
    session(SESSION_ID_FIELD, student.name);
    return redirect(routes.HTML.html(exercise));
  }
  
  public Result login() {
    return ok(login.render(form(Login.class)));
  }
  
  private Student findOrCreateStudent(String userName) {
    if(Student.find.byId(userName) == null) {
      Student newStudent = new Student();
      newStudent.name = userName;
      newStudent.save();
      Path path = Paths.get("solutions/" + userName);
      if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
        try {
          Files.createDirectory(path);
        } catch (IOException e) {
        }
    }
    return Student.find.byId(userName);
  }
  
  public Result logout() {
    session().clear();
    return ok(login.render(form(Login.class)));
  }
  
  public Result authenticate() {
    Form<Login> loginForm = form(Login.class).bindFromRequest();
    if(loginForm.hasErrors()) {
      return badRequest(login.render(loginForm));
    } else {
      Student student = findOrCreateStudent(loginForm.get().name);
      session().clear();
      session(SESSION_ID_FIELD, student.name);
      return redirect(routes.Application.index());
    }
  }
  
  public static class Login {
    public String name;
    public String password;
  }
  
}
