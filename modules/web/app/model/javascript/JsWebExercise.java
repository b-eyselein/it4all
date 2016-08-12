package model.javascript;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class JsWebExercise extends Model {

  public static Finder<Integer, JsWebExercise> finder = new Finder<>(JsWebExercise.class);

  @Id
  public int id;

  public String title;

  @Column(columnDefinition = "text")
  public String text;

  public String anterior;

  public String posterior;

  public String declaration;

  public List<JsWebTest> getTests() {
    String actionElement = "//input[@type='button']";
    String conditionElement = "//span[@id='counter']";

    List<JsWebTest> tests = new ArrayList<>(10);
    for(int i = 0; i < 5; i++)
      tests.add(new JsWebTest(conditionElement, i + "", (i + 1) + "", actionElement));

    return tests;
  }

}
