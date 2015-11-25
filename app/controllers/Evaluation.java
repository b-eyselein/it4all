package controllers;

import java.util.Map;

import model.feedback.Feedback;
import model.feedback.Feedback.Note;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.evaluation.eval;
import views.html.evaluation.submit;

public class Evaluation extends Controller {
  
  public Result index() {
    return ok(eval.render("Evaluation"));
  }
  
  public Result submit() {
    Map<String, String[]> evaluation = request().body().asFormUrlEncoded();
    String sinnHtml = evaluation.get("sinn-html")[0];
    String sinnExcel = evaluation.get("sinn-excel")[0];
    String nutzenHtml = evaluation.get("nutzen-html")[0];
    String nutzenExcel = evaluation.get("nutzen-excel")[0];
    
    String[] bedienungHtml = evaluation.get("bedienung-html");
    String[] feedbackHtml = evaluation.get("feedback-html");
    String[] korrekturHtml = evaluation.get("korrektur-html");
    String[] bedienungExcel = evaluation.get("bedienung-excel");
    String[] feedbackExcel = evaluation.get("feedback-excel");
    String[] korrekturExcel = evaluation.get("korrektur-excel");
    
    Feedback fb = new Feedback();
    fb.sinnHtml = sinnHtml != null ? Integer.parseInt(sinnHtml) : -1;
    fb.sinnExcel = sinnExcel != null ? Integer.parseInt(sinnExcel) : -1;
    fb.nutzenHtml = nutzenHtml != null ? Integer.parseInt(nutzenHtml) : -1;
    fb.nutzenExcel = nutzenExcel != null ? Integer.parseInt(nutzenExcel) : -1;
    
    fb.bedienungHtml = Note.values()[bedienungHtml != null ? Integer.parseInt(bedienungHtml[0]) : 0];
    fb.feedbackHtml = Note.values()[feedbackHtml != null ? Integer.parseInt(feedbackHtml[0]) : 0];
    fb.korrekturHtml = Note.values()[korrekturHtml != null ? Integer.parseInt(korrekturHtml[0]) : 0];
    fb.bedienungExcel = Note.values()[bedienungExcel != null ? Integer.parseInt(bedienungExcel[0]) : 0];
    fb.feedbackExcel = Note.values()[feedbackExcel != null ? Integer.parseInt(feedbackExcel[0]) : 0];
    fb.korrekturExcel = Note.values()[korrekturExcel != null ? Integer.parseInt(korrekturExcel[0]) : 0];
    
    fb.kommentarHtml = evaluation.get("kommentar-html")[0];
    fb.kommentarExcel = evaluation.get("kommentar-excel")[0];
    
    fb.save();
    
    return ok(submit.render("Evaluation", fb));
  }
  
}
