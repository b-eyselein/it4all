package controllers;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import model.feedback.Feedback;
import model.feedback.Feedback.Note;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.evaluation.eval;
import views.html.evaluation.submit;

public class Evaluation extends Controller {

  private static final List<String> EVALUATED_TOOLS = Arrays.asList("HTML", "CSS", "Javascript");

  private FormFactory factory;

  @Inject
  public Evaluation(FormFactory theFactory) {
    factory = theFactory;
  }

  public Result index() {
    return ok(eval.render("Evaluation", EVALUATED_TOOLS));
  }

  public Result submit() {
    // FIXME: evaluation in subprojects -> for every single project?
    DynamicForm form = factory.form().bindFromRequest();

    String sinnHtml = form.get("sinn-html");
    String sinnExcel = form.get("sinn-excel");
    String nutzenHtml = form.get("nutzen-html");
    String nutzenExcel = form.get("nutzen-excel");

    String bedienungHtml = form.get("bedienung-html");
    String feedbackHtml = form.get("feedback-html");
    String korrekturHtml = form.get("korrektur-html");
    String bedienungExcel = form.get("bedienung-excel");
    String feedbackExcel = form.get("feedback-excel");
    String korrekturExcel = form.get("korrektur-excel");

    Feedback fb = new Feedback();
    fb.sinnHtml = sinnHtml != null ? Integer.parseInt(sinnHtml) : -1;
    fb.sinnExcel = sinnExcel != null ? Integer.parseInt(sinnExcel) : -1;
    fb.nutzenHtml = nutzenHtml != null ? Integer.parseInt(nutzenHtml) : -1;
    fb.nutzenExcel = nutzenExcel != null ? Integer.parseInt(nutzenExcel) : -1;

    fb.bedienungHtml = Note.values()[bedienungHtml != null ? Integer.parseInt(bedienungHtml) : 0];
    fb.feedbackHtml = Note.values()[feedbackHtml != null ? Integer.parseInt(feedbackHtml) : 0];
    fb.korrekturHtml = Note.values()[korrekturHtml != null ? Integer.parseInt(korrekturHtml) : 0];
    fb.bedienungExcel = Note.values()[bedienungExcel != null ? Integer.parseInt(bedienungExcel) : 0];
    fb.feedbackExcel = Note.values()[feedbackExcel != null ? Integer.parseInt(feedbackExcel) : 0];
    fb.korrekturExcel = Note.values()[korrekturExcel != null ? Integer.parseInt(korrekturExcel) : 0];

    fb.kommentarHtml = form.get("kommentar-html");
    fb.kommentarExcel = form.get("kommentar-excel");

    fb.save();

    return ok(submit.render("Evaluation", fb));
  }

}
