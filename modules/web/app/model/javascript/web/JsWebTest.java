package model.javascript.web;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.openqa.selenium.WebDriver;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Success;
import model.javascript.web.Action.ActionType;

@Entity
public class JsWebTest extends Model {

  @Id
  public int id;

  @ManyToOne
  @JsonBackReference
  public JsWebExercise exercise;

  @Enumerated(EnumType.STRING)
  public ActionType actiontype;
  public String actionElementAsString;
  public String otherActionFeatures;

  @OneToMany(mappedBy = "pre")
  @JsonManagedReference
  public List<Requirement> preconditions;

  // @OneToOne
  // @JoinColumn(name = "precondition")
  // public Requirement precondition;

  // private Action action;

  @OneToMany(mappedBy = "post")
  @JsonManagedReference
  public List<Requirement> postconditions;

  public Action getAction() {
    return Action.instantiate(actiontype, actionElementAsString, otherActionFeatures);
  }

  public JsWebTestResult test(WebDriver driver) {
    Action action = Action.instantiate(actiontype, actionElementAsString, otherActionFeatures);

    List<String> messages = new LinkedList<>();

    boolean preconditionsSatisfied = true;
    for(Requirement precondition: preconditions)
      preconditionsSatisfied = preconditionsSatisfied && precondition.test(driver);
    if(!preconditionsSatisfied)
      return new JsWebTestResult(this, Success.NONE, "Vorbedingung(en) konnten nicht verifiziert werden!");
    messages.add("Vorbedingung(en) konnten erfolgreich verifizert werden.");

    boolean actionPerformed = action == null || action.perform(driver);
    if(!actionPerformed)
      return new JsWebTestResult(this, Success.NONE, "Aktion konnte nicht ausgeführt werden!");
    messages.add("Aktion konnte erfolgreich ausgeführt werden.");

    boolean postconditionSatisfied = true;
    for(Requirement postcondition: postconditions)
      postconditionSatisfied = postconditionSatisfied && postcondition.test(driver);
    if(!postconditionSatisfied)
      return new JsWebTestResult(this, Success.NONE, "Nachbedingung(en) konnten nicht verifiziert werden!");
    messages.add("Nachbedingung(en) konnten erfolgreich verifiziert werden.");
    
    return new JsWebTestResult(this, Success.COMPLETE, messages);
  }

}
