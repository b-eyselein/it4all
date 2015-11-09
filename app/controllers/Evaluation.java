package controllers;

import java.util.Map;

import model.Student;
import model.feedback.Feedback;
import model.feedback.Feedback.Note;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.evaluation.eval;
import views.html.evaluation.submit;

public class Evaluation extends Controller {
  
  public Result index(String user) {
    Student student = Student.find.byId(user);
    if(student == null)
      return redirect("/login");
    session(Application.SESSION_ID_FIELD, user);
    return ok(eval.render("Evaluation", student));
  }
  
  public Result submit() {
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(student == null)
      return redirect("/login");
    
    Map<String, String[]> evaluation = request().body().asFormUrlEncoded();
    String[] bedienung = evaluation.get("bedienung");
    String[] feedback = evaluation.get("feedback");
    String[] korrektur = evaluation.get("korrektur");
    
    Feedback fb = new Feedback();
    fb.bedienung = Note.values()[bedienung != null ? Integer.parseInt(bedienung[0]) : 0];
    fb.feedback = Note.values()[feedback != null ? Integer.parseInt(feedback[0]) : 0];
    fb.korrektur = Note.values()[korrektur != null ? Integer.parseInt(korrektur[0]) : 0];
    fb.kommentar = evaluation.get("kommentar")[0];
    fb.save();
    
    return ok(submit.render("Evaluation", student, fb));
  }
  
}
