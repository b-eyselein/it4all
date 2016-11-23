package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.feedback.Feedback;
import model.feedback.Feedback.EvaluatedTool;
import model.feedback.FeedbackKey;
import model.feedback.Mark;
import model.feedback.YesNoMaybe;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.evaluation.eval;
import views.html.evaluation.submit;

@Authenticated(Secured.class)
public class Evaluation extends Controller {

  private FormFactory factory;

  @Inject
  public Evaluation(FormFactory theFactory) {
    factory = theFactory;
  }

  public Result index() {
    User user = UserManagement.getCurrentUser();
    List<Feedback> toEvaluate = Arrays.stream(Feedback.EvaluatedTool.values()).map(tool -> {
      FeedbackKey key = new FeedbackKey(user.name, tool);
      Feedback feedback = Feedback.finder.byId(key);
      if(feedback == null)
        feedback = new Feedback(key);
      return feedback;
    }).collect(Collectors.toList());

    return ok(eval.render(user, toEvaluate));
  }

  public Result submit() {
    User user = UserManagement.getCurrentUser();

    DynamicForm form = factory.form().bindFromRequest();

    List<Feedback> evaluation = Arrays.stream(EvaluatedTool.values()).map(tool -> readFeedback(user, form, tool))
        .collect(Collectors.toList());

    for(Feedback f: evaluation)
      f.save();

    return ok(submit.render(user, evaluation));
  }

  private Feedback readFeedback(User user, DynamicForm form, EvaluatedTool tool) {
    FeedbackKey key = new FeedbackKey(user.name, tool);
    Feedback feedback = Feedback.finder.byId(key);
    if(feedback == null)
      feedback = new Feedback(key);

    String evaluatedTool = tool.toString().toLowerCase();

    feedback.sense = YesNoMaybe.valueOf(form.get("sense-" + evaluatedTool));

    feedback.used = YesNoMaybe.valueOf(form.get("used-" + evaluatedTool));

    feedback.usability = Mark.valueOf(form.get("usability-" + evaluatedTool));

    feedback.feedback = Mark.valueOf(form.get("feedback-" + evaluatedTool));

    feedback.fairness = Mark.valueOf(form.get("fairness-" + evaluatedTool));

    feedback.comment = form.get("comment-" + evaluatedTool);

    return feedback;
  }

}
