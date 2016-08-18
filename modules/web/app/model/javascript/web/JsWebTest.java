package model.javascript.web;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.openqa.selenium.WebDriver;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Success;

@Entity
public class JsWebTest extends Model {
  
  @Id
  public int id;
  
  @ManyToOne
  @JsonBackReference
  public JsWebExercise exercise;
  
  @OneToMany(mappedBy = "pre")
  @JsonManagedReference
  public List<Requirement> preconditions;
  
  @Embedded
  public Action action;
  
  @OneToMany(mappedBy = "post")
  @JsonManagedReference
  public List<Requirement> postconditions;
  
  public JsWebTestResult test(WebDriver driver) {
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
