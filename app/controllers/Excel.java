package controllers;

import model.Student;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.excel;

public class Excel extends Controller {
  
  public Result index() {
    String userName = session("id");
    if(userName == null)
      return redirect("/login");
    Student user = Student.find.byId(userName);
    
    return ok(excel.render(user));
  }
  
  public Result upload() {
    String userName = session("id");
    if(userName == null)
      return redirect("/login");
    Student user = Student.find.byId(userName);
    
    return ok(excel.render(user));
  }
  
}
