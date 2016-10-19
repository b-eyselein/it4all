package model.feedback;

import java.util.OptionalDouble;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Feedback extends Model {
  
  public enum Note {
    NO_FEEDBACK, SEHR_GUT, GUT, EHER_SCHLECHT, SCHLECHT;
  }

  public static Finder<Integer, Feedback> finder = new Finder<>(Feedback.class);

  @Id
  public int id;
  public int sinnHtml;

  public int sinnExcel;
  public int nutzenHtml;

  public int nutzenExcel;
  public Note bedienungHtml;

  public Note bedienungExcel;
  public Note feedbackHtml;

  public Note feedbackExcel;
  public Note korrekturHtml;

  public Note korrekturExcel;
  public String kommentarHtml;

  public String kommentarExcel;

  public static double getDurchschnittBedienungExcel() {
    OptionalDouble ret = finder.all().stream().filter(feedback -> feedback.bedienungExcel != Note.NO_FEEDBACK)
        .mapToInt(feedback -> feedback.bedienungExcel.ordinal()).average();
    return ret.isPresent() ? ret.getAsDouble() : 0.;
  }

  public static double getDurchschnittBedienungHtml() {
    OptionalDouble ret = finder.all().stream().filter(feedback -> feedback.bedienungHtml != Note.NO_FEEDBACK)
        .mapToInt(feedback -> feedback.bedienungHtml.ordinal()).average();
    return ret.isPresent() ? ret.getAsDouble() : 0.;
  }

  public static double getDurchschnittFeedbackExcel() {
    OptionalDouble ret = finder.all().stream().filter(feedback -> feedback.feedbackExcel != Note.NO_FEEDBACK)
        .mapToInt(feedback -> feedback.feedbackExcel.ordinal()).average();
    return ret.isPresent() ? ret.getAsDouble() : 0.;
  }

  public static double getDurchschnittFeedbackHtml() {
    OptionalDouble ret = finder.all().stream().filter(feedback -> feedback.feedbackHtml != Note.NO_FEEDBACK)
        .mapToInt(feedback -> feedback.feedbackHtml.ordinal()).average();
    return ret.isPresent() ? ret.getAsDouble() : 0.;
  }

  public static double getDurchschnittKorrekturExcel() {
    OptionalDouble ret = finder.all().stream().filter(feedback -> feedback.korrekturExcel != Note.NO_FEEDBACK)
        .mapToInt(feedback -> feedback.korrekturExcel.ordinal()).average();
    return ret.isPresent() ? ret.getAsDouble() : 0.;
  }

  public static double getDurchschnittKorrekturHtml() {
    OptionalDouble ret = finder.all().stream().filter(feedback -> feedback.korrekturHtml != Note.NO_FEEDBACK)
        .mapToInt(feedback -> feedback.korrekturHtml.ordinal()).average();
    return ret.isPresent() ? ret.getAsDouble() : 0.;
  }

  public static int getNutzenExcelGesamt() {
    return finder.all().stream().mapToInt(feedback -> feedback.nutzenExcel).sum();
  }

  public static int getNutzenHtmlGesamt() {
    return finder.all().stream().mapToInt(feedback -> feedback.nutzenHtml).sum();
  }

  public static int getSinnExcelGesamt() {
    return finder.all().stream().mapToInt(feedback -> {
      if(feedback.sinnExcel > 0)
        return feedback.sinnExcel;
      else
        return 0;
    }).sum();
  }

  public static int getSinnHtmlGesamt() {
    return finder.all().stream().mapToInt(feedback -> {
      if(feedback.sinnHtml > 0)
        return feedback.sinnHtml;
      else
        return 0;
    }).sum();
  }
}
