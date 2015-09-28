package controllers;

import java.util.List;

import model.Task;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.html;

public class HTML extends Controller {
  
  HtmlCorrector corrector = new HtmlCorrector(STANDARD_EXERCISE);
  
  // TODO: lese Standardaufgabe aus Datei/Datenbank??
  private static final HtmlExercise STANDARD_EXERCISE = new HtmlExercise(
      "Erstellen sie eine HTML-Seite mit folgenden Inhalten:",
      "<!DOCTYPE html>\n<html>\n<head>\n    <title>Titel</title>\n</head>\n<body>\n    Body...\n</body>\n</html>",
      new Task("Eine geordnete Liste, die eine ungeordnete Liste beinhaltet", 2), new Task(
          "Einen Link auf eine beliebige Website", 1), new Task("Eine Tabelle 3 x 3 mit Zahlen", 1), new Task(
          "Ein Eingabefeld, dass nur Eingaben vom Typ \"email\" zulässt", 1),
      new Task("Ein Bild", 1));
  
  public Result html() {
    return ok(html.render(STANDARD_EXERCISE));
  }
  
  public WebSocket<String> getWebSocket() {
    return WebSocket.whenReady((in, out) -> {
      in.onMessage(solution -> {
        List<String> errors = corrector.correct(solution);
        out.write(String.join("\n", errors));
      });
      
      in.onClose(() -> {
        // TODO: speichere Loesung??
      });
    });
  }
}
