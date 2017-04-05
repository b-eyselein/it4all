package controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import controllers.core.AController;
import model.AdminSecured;
import model.feedback.Feedback;
import model.feedback.Feedback.EvaluatedTool;
import model.feedback.Mark;
import model.feedback.YesNoMaybe;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(AdminSecured.class)
public class AdminController extends AController {
  
  private static String renderMarks(String evaluated, Map<Mark, Long> marks) {
    return "<p>" + evaluated + ": " + marks.get(Mark.VERY_GOOD) + " Sehr gut, " + marks.get(Mark.GOOD) + " Gut, "
        + marks.get(Mark.NEUTRAL) + " Neutral, " + marks.get(Mark.BAD) + " Schlecht, " + marks.get(Mark.VERY_BAD)
        + " Sehr schlecht und " + marks.get(Mark.NO_MARK) + " Enthaltungen</p>";
  }
  
  private static String renderYesNoMaybe(String evaluated, Map<YesNoMaybe, Long> marks) {
    return "<p>" + evaluated + ": " + marks.get(YesNoMaybe.YES) + " Ja, " + marks.get(YesNoMaybe.NO) + " Nein und "
        + marks.get(YesNoMaybe.MAYBE) + " Enthaltungen</p>";
  }
  
  public Result evaluation() {
    String evaluation = evaluate(Feedback.finder.all());
    
    return ok(views.html.evaluation.stats.render(getUser(), evaluation));
  }
  
  public Result index() {
    return ok(views.html.admin.render(getUser()));
  }
  
  private String evaluate(EvaluatedTool key, List<Feedback> feedbackForTool) {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    
    builder.append("<div>");
    builder.append("<h2>" + key + "</h2>");
    
    // Sense
    Map<YesNoMaybe, Long> sinnvoll = feedbackForTool.stream()
        .collect(Collectors.groupingBy(f -> f.sense, Collectors.counting()));
    builder.append(renderYesNoMaybe("Sinnvoll", sinnvoll));
    
    // Usage
    Map<YesNoMaybe, Long> usage = feedbackForTool.stream()
        .collect(Collectors.groupingBy(f -> f.used, Collectors.counting()));
    builder.append(renderYesNoMaybe("Genutzt", usage));
    
    // usability
    Map<Mark, Long> usability = feedbackForTool.stream()
        .collect(Collectors.groupingBy(f -> f.usability, Collectors.counting()));
    builder.append(renderMarks("Benutzbarkeit", usability));
    
    // feedback
    Map<Mark, Long> feedback = feedbackForTool.stream()
        .collect(Collectors.groupingBy(f -> f.feedback, Collectors.counting()));
    builder.append(renderMarks("Feedback", feedback));
    
    // fairness
    Map<Mark, Long> fairness = feedbackForTool.stream()
        .collect(Collectors.groupingBy(f -> f.fairness, Collectors.counting()));
    builder.append(renderMarks("Fairness der Korrektur", fairness));
    
    // comment
    String commentsAsList = feedbackForTool.stream().filter(t -> !t.comment.isEmpty()).map(t -> t.comment)
        .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
    builder.append("<div>Kommentare:" + commentsAsList + "</div>");
    
    builder.append("</div>");
    return builder.toString();
  }
  
  private String evaluate(List<Feedback> all) {
    StringBuilder builder = new StringBuilder();
    
    Map<Feedback.EvaluatedTool, List<Feedback>> sorted = all.stream()
        .collect(Collectors.groupingBy(Feedback::theTool, Collectors.toList()));
    
    builder.append("<p>Gesamte abgegebene Bewertungen: " + (all.size() / Feedback.EvaluatedTool.values().length));
    
    for(Entry<EvaluatedTool, List<Feedback>> entries: sorted.entrySet())
      builder.append(evaluate(entries.getKey(), entries.getValue()));
    
    return builder.toString();
  }
  
}
