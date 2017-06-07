package model.feedback;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import io.ebean.Finder;
import io.ebean.Model;
import play.twirl.api.Html;

@Entity
public class Feedback extends Model {
  
  public enum EvaluatedTool {
    HTML, CSS, JSWEB, JS, SQL;
  }
  
  public static final Finder<FeedbackKey, Feedback> finder = new Finder<>(Feedback.class);
  
  private static final String DIV_START = "<div class=\"form-group\">\n";
  private static final String DIV_END = "</div>\n</div>\n";
  private static final String LABEL_END = "</label>\n";

  @EmbeddedId
  public FeedbackKey key;
  
  @Enumerated(EnumType.STRING)
  public YesNoMaybe sense = YesNoMaybe.MAYBE;
  
  @Enumerated(EnumType.STRING)
  public YesNoMaybe used = YesNoMaybe.MAYBE;
  
  @Enumerated(EnumType.STRING)
  public Mark usability = Mark.NO_MARK;
  
  @Enumerated(EnumType.STRING)
  public Mark feedback = Mark.NO_MARK;
  
  @Enumerated(EnumType.STRING)
  public Mark fairness = Mark.NO_MARK;
  
  @Column(columnDefinition = "text")
  public String comment = "";
  
  public Feedback(FeedbackKey theKey) {
    key = theKey;
  }

  private static String renderComment(String evaluatedTool, String currentComment) {
    StringBuilder builder = new StringBuilder();
    builder.append(DIV_START);
    builder.append("<label class=\"control-label col-sm-4\" for=\"comment-" + evaluatedTool
        + "\">Verbesserungsvorschläge:" + LABEL_END);
    
    builder.append("<div class=\"col-sm-8\">\n");
    builder.append("<textarea class=\"form-control\" rows=\"5\" name=\"comment-" + evaluatedTool + "\" id=\"comment-"
        + evaluatedTool + "\"   placeholder=\"Verbesserungsvorschläge\">" + currentComment + "</textarea>\n");
    builder.append("<p>Bitte beschr&auml;nken Sie sich auf 250 Zeichen.</p>\n");
    
    builder.append(DIV_END);
    return builder.toString();
  }
  
  private static String renderMarked(String evaluatedAspect, String evaluatedTool, String question, Mark current) {
    StringBuilder builder = new StringBuilder();
    builder.append(DIV_START);
    builder.append("<label class=\"control-label col-sm-4\">" + question + LABEL_END);
    
    for(Mark note: model.feedback.Mark.values()) {
      builder.append("<div class=\"col-sm-1\"><div class=\"radio\">\n");
      builder.append("<label><input type=\"radio\" name=\"" + evaluatedAspect + "-" + evaluatedTool + "\" value=\""
          + note + "\"" + (note == current ? " checked" : "") + ">" + note.getGerman() + LABEL_END);
      builder.append(DIV_END);
    }
    
    builder.append("</div>\n");
    
    return builder.toString();
  }
  
  private static String renderYesNoMaybe(String evaluatedAspect, String evaluatedTool, String question,
      YesNoMaybe current) {
    StringBuilder builder = new StringBuilder();
    builder.append(DIV_START);
    builder.append("<label class=\"control-label col-sm-4\">" + question + LABEL_END);
    
    for(YesNoMaybe answer: YesNoMaybe.values()) {
      builder.append("<div class=\"col-sm-2\"><div class=\"radio\">\n");
      builder.append("<label><input type=\"radio\" name=\"" + evaluatedAspect + "-" + evaluatedTool + "\" value=\""
          + answer + "\"" + (answer == current ? " checked" : "") + ">" + answer.getGerman() + LABEL_END);
      builder.append(DIV_END);
    }
    
    builder.append("</div>\n");
    return builder.toString();
  }
  
  public Html render() {
    String evaluatedTool = key.tool.toString().toLowerCase();
    
    StringBuilder builder = new StringBuilder();
    builder.append("<h2>Evaluation der " + key.tool + "-Korrektur</h2>");
    
    // Sinn, Nutzen
    builder.append(renderYesNoMaybe("sense", evaluatedTool, "Finden Sie dieses Tool sinnvoll?", sense));
    builder.append(renderYesNoMaybe("used", evaluatedTool, "Haben Sie dieses Tool genutzt?", used));
    
    // Usability, Fairness und correction
    builder.append(renderMarked("usability", evaluatedTool, "Allgemeine Bedienbarkeit", usability));
    builder.append(renderMarked("feedback", evaluatedTool, "Gestaltung des Feedbacks", feedback));
    builder.append(renderMarked("fairness", evaluatedTool, "Fairness der Evaluation", fairness));
    
    // FIXME: Kommentar!
    builder.append(renderComment(evaluatedTool, comment));
    
    return new Html(builder.toString());
  }

  public EvaluatedTool theTool() {
    return key.tool;
  }
  
}
