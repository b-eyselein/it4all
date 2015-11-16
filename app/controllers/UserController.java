package controllers;

import java.util.List;

import model.Grading;
import model.user.Student;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.user;

public class UserController extends Controller {
  
  public Result index() {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    
    List<Grading> gradings = Grading.finder.where().eq("student_name", student.name).findList();
    
    return ok(user.render("User", student, gradings));
  }
  
}
