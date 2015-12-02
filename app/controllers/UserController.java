package controllers;

import java.util.List;

import model.Grading;
import model.user.Student;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user;

@Security.Authenticated(Secured.class)
public class UserController extends Controller {
  
  public Result index() {
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    List<Grading> gradings = Grading.finder.where().eq("student_name", student.name).findList();
    return ok(user.render("User", student, gradings));
  }
  
}
