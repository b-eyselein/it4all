package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Exercise;

@Entity
public class ProgrammingExercise extends Exercise {
  
  public static final Finder<Integer, ProgrammingExercise> finder = new Finder<>(ProgrammingExercise.class);

  public String declaration; // NOSONAR

  public String functionname; // NOSONAR

  public String sampleSolution; // NOSONAR

  public int inputcount; // NOSONAR

  @OneToMany(mappedBy = "exercise")
  @JsonManagedReference
  public List<TestData> functiontests;
  
  public ProgrammingExercise(int theId) {
    super(theId);
  }

  public int getInputcount() {
    return inputcount;
  }
  
  @Override
  public String renderData() {
    // TODO Auto-generated method stub
    StringBuilder builder = new StringBuilder();
    builder.append("<div class=\"col-md-6\">");
    builder.append("<div class=\"panel panel-default\">");
    builder.append("<div class=\"panel-heading\">Aufgabe " + id + ": " + title + DIV_END);
    builder.append("<div class=\"panel-body\">");
    builder.append("<p>Aufgabentext: " + text + "</p>");
    
    // Declaration and sample solution
    builder.append("<div class=\"row\">");
    builder.append("<div class=\"col-md-6\"><p>Angabe: <pre>" + declaration + "</pre></p>" + DIV_END);
    builder.append("<div class=\"col-md-6\"><p>Musterlösung: <pre>" + sampleSolution + "</pre></p>" + DIV_END);
    builder.append(DIV_END);
    
    // FIXME: Inputs and Output, Tests
    // builder.append("<table class=\"table\">");
    // builder.append("<thead><tr><th>Id</th>"
    // + getInputTypes().stream().map(JsDataType::toString)
    // .collect(Collectors.joining("</th><th>Input: ", "<th>Input: ", "</th>"))
    // + "<th>Output: " + returntype + "</th></tr></thead><tbody>");
    // for(JsTestData test: functionTests)
    // builder.append("<tr><td>" + test.getId() + "</td>"
    // + test.getInput().stream().collect(Collectors.joining("</td><td>",
    // "<td>", "</td>")) + "<td>"
    // + test.getOutput() + "</td></tr>");
    // builder.append("</tbody></table>");
    
    builder.append(DIV_END + DIV_END + DIV_END);
    return builder.toString();
  }

}
