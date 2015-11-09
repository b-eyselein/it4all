package controllers;

import model.Student;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.excel.excel;
import views.html.excel.exceloverview;
import views.html.excel.excelcorrect;

public class Excel extends Controller {
  
  public Result index() {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    return ok(exceloverview.render(user));
  }
  
  public Result exercise(int exercise) {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    if(exercise == -1)
      return redirect("/index");
    return ok(excel.render(user));
  }
  
  public Result upload() {
    String userName = session("id");
    if(userName == null)
      return redirect("/login");
    Student user = Student.find.byId(userName);
    
    return ok(excelcorrect.render(user));
  }
  
}
