package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.sql;

public class SQL extends Controller {
  
  public Result index() {
    return ok(sql.render());
  }
  
}