package controllers;

import static play.data.Form.form;
import model.Student;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.login;

public class Application extends Controller {
  
  private static final String SESSION_ID_FIELD = "id";
  private static final String SESSION_PW_FIELD = "pw";
  
  public Result index() {
    if(session(SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student student = Student.find.byId(session(SESSION_ID_FIELD));
    return ok(index.render(student));
  }
  
  public Result login() {
    return ok(login.render(form(Login.class)));
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
      session().clear();
      session(SESSION_ID_FIELD, loginForm.get().name);
      return redirect(routes.Application.index());
    }
  }
  
  public Result directLogin(String id, String pw, String type, int exercise) {
    if(testLoginData(id, pw)) {
      if(session(SESSION_ID_FIELD) == null) {
        session().put(SESSION_ID_FIELD, id);
        session().put(SESSION_PW_FIELD, pw);
      }
      switch(type) {
      case "html":
        return redirect("/html?exercise=" + exercise);
      case "php":
        return redirect("/php?&exercise=" + exercise);
      default:
        return redirect("/");
      }
    } else {
      return forbidden("Login stimmt nicht!");
    }
  }
  
  public boolean testLoginData(String id, String pw) {
    return true;
  }
  
  public static class Login {
    public String name;
    public String password;
    
    public String validate() {
      if(Student.authenticate(name, password) == null) {
        return "Invalid user or password";
      }
      return null;
    }
    
  }
  
}
