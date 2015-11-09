package controllers;

import model.Student;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.excel.excel;
import views.html.excel.excelcorrect;

public class Excel extends Controller {
  
  public Result index(String name) {
    if(name == null || name.isEmpty())
      return redirect("/login");
    Student user = Student.find.byId(name);
    
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
