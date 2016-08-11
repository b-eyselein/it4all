package model.javascript;

import play.twirl.api.Html;

public class JsWebExercise {
  
  public int id = 1;
  
  public String title = "Klickzähler!";
  
  public String text = "Implementieren Sie die folgende Funktion <code>blendeAus()</code>, die aufgerufen wird, wenn auf den Knopf gedrückt wird."
      + " Sie soll den folgenden Text mit der id \"theText\" beim ersten Drücken ausblenden und beim erneuten Drücken wieder einblenden!";
  
  public String anterior = "<!DOCTYPE html>\n<html>\n<head>\n  <title>Aufgabe</title>\n  <script type=\"text/javascript\">";
  
  public String posterior = "  </script>\n</head>\n\n<body>\n"
      + "  <h3>Erhöhen Sie den Counter um 1, wenn der Button gedrückt wird!</h3>\n"
      + "  <input type=\"button\" onclick=\"count();\" value=\"Push me!\">\n"
      + "  <p>Count: <span id=\"counter\">0</span></p>\n</body>\n<html>";
  
  public String declaration = "function count() {\n  \n}";
  
  public Html vorschau() {
    return new Html(anterior + declaration + posterior);
  }
  
}
