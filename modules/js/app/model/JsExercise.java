package model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;

@Entity
public class JsExercise extends Exercise {

  public enum JsDataType {
    BOOLEAN, NUMBER, STRING, SYMBOL, UNDEFINED, NULL, OBJECT;
  }

  public static final Finder<Integer, JsExercise> finder = new Finder<>(JsExercise.class);

  @Id
  public int id;
  
  @Column(columnDefinition = "text")
  public String text;

  public String declaration; // NOSONAR

  public String functionname; // NOSONAR

  public String sampleSolution; // NOSONAR

  public String inputtypes; // NOSONAR

  public int inputcount; // NOSONAR

  @Enumerated(EnumType.STRING)
  public JsDataType returntype;

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<JsTest> functionTests;

  public JsExercise(int theId) {
    id = theId;
  }

  @Override
  public int getId() {
    return id;
  }

  public List<JsDataType> getInputTypes() {
    return Arrays.stream(inputtypes.split("#")).map(JsDataType::valueOf).collect(Collectors.toList());
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getText() {
    return text;
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + getId() + ": " + getTitle() + DIV_END);
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Aufgabentext: " + text + "</p>");

    // Declaration and sample solution
    builder.append("<div class=\"row\">");
    builder.append("<div class=\"col-md-6\"><p>Angabe: <pre>" + declaration + "</pre></p>" + DIV_END);
    builder.append("<div class=\"col-md-6\"><p>Musterlösung: <pre>" + sampleSolution + "</pre></p>" + DIV_END);
    builder.append(DIV_END);

    // Inputs and Output, Tests
    builder.append("<table class=\"table\">");
    builder.append("<thead><tr><th>Id</th>"
        + getInputTypes().stream().map(JsDataType::toString)
            .collect(Collectors.joining("</th><th>Input: ", "<th>Input: ", "</th>"))
        + "<th>Output: " + returntype + "</th></tr></thead><tbody>");
    for(JsTest test: functionTests)
      builder.append("<tr><td>" + test.getId() + "</td>"
          + test.getInput().stream().collect(Collectors.joining("</td><td>", "<td>", "</td>")) + "<td>"
          + test.getOutput() + "</td></tr>");
    builder.append("</tbody></table>");

    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }

}
