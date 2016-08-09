package model.javascript;

import play.twirl.api.Html;

public class JsWebExercise {
  
  public int id = 1;
  
  public String text = "Implementieren Sie die folgende Funktion <code>blendeAus()</code>, die aufgerufen wird, wenn auf den Knopf gedrückt wird."
      + " Sie soll den folgenden Text mit der id \"theText\" beim ersten Drücken ausblenden und beim erneuten Drücken wieder einblenden!";
  
  public String anterior = "<!DOCTYPE html>\n<html>\n<head>\n  <title>Aufgabe</title>\n  <script type=\"text/javascript\">";
  
  public String posterior = "  </script>\n</head>\n\n<body>\n"
      + "  <h3>Blenden Sie diesen Text aus, wenn der Button gedrückt wird, und wieder ein, wenn er wieder gedrückt wird!</h3>\n"
      + "  <input type=\"button\" onclick=\"blendeAus();\" value=\"Blende den Text aus!\">\n"
      + "  <p id=\"theText\">Dies ist der Text zum Ausblenden</p>\n</body>\n<html>";
  
  public String declaration = "function blendeAus() {\n  \n}";
  
  public Html vorschau() {
    return new Html(anterior + declaration + posterior);
  }
  
}
