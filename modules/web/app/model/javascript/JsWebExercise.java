package model.javascript;

import java.util.ArrayList;
import java.util.List;

public class JsWebExercise {

  public int id = 1;

  public String title = "Klickzähler!";

  public String text = "Implementieren Sie die folgende Funktion <code>count()</code>, die aufgerufen wird, wenn auf den Knopf gedrückt wird."
      + " Sie soll den folgenden Text mit der id \"theText\" beim ersten Drücken ausblenden und beim erneuten Drücken wieder einblenden!";

  public String anterior = "<!DOCTYPE html>\n<html>\n<head>\n  <title>Aufgabe</title>\n  <script type=\"text/javascript\">";

  public String posterior = "  </script>\n</head>\n\n<body>\n"
      + "  <h3>Erhöhen Sie den Counter um 1, wenn der Button gedrückt wird!</h3>\n"
      + "  <input type=\"button\" onclick=\"count();\" value=\"Push me!\">\n"
      + "  <p>Count: <span id=\"counter\">0</span></p>\n</body>\n<html>";

  // FIXME: change back!
  public String declaration = "function count() {\n  var element = document.getElementById(\"counter\");\n  var count = parseInt(element.innerHTML) + 1;\n  element.innerHTML = count;\n}";

  public List<JsWebTest> getTests() {
    String actionElement = "//input[@type='button']";
    String conditionElement = "//span[@id='counter']";

    List<JsWebTest> tests = new ArrayList<>(10);
    for(int i = 0; i < 5; i++)
      tests.add(new JsWebTest(conditionElement, i + "", (i + 1) + "", actionElement));

    return tests;
  }

}
