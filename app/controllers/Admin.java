package controllers;

import java.util.List;

import model.feedback.Feedback;
import model.user.Administrator;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.admin;

public class Admin extends Controller {

  public Result index() {
	  List<Feedback> feedbackList = Feedback.finder.all();
    return ok(admin.render(new Administrator(), feedbackList));
  }
  
}
