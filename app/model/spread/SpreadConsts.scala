package model.spread

import model.core.FileExConsts

object SpreadConsts extends FileExConsts {

  val COMMENT_CHART_CORRECT                        = "Diagramm(e) richtig."
  val COMMENT_CHART_FALSE                          = "Es sollten keine Diagramme erstellt werden."
  val COMMENT_CHART_NUM_CORRECT                    = "Richtige Anzahl Diagramme gefunden."
  val COMMENT_CHART_NUM_INCORRECT_VAR              = "Falsche Anzahl Diagramme im Dokument (Erwartet: %d, Gefunden: %d)."
  val COMMENT_CONDITIONAL_FORMATTING_CORRECT       = "Bedingte Formatierung richtig."
  val COMMENT_CONDITIONAL_FORMATTING_NUM_FALSE     = "Keine bedingte Formatierung notwendig."
  val COMMENT_CONDITIONAL_FORMATTING_NUM_INCORRECT = "Bedingte Formatierung falsch. Keine bedingte Formatierung gefunden."
  val COMMENT_FORMULA_HINT_VAR                     = "Verwenden Sie die Funktion %s."
  val COMMENT_FORMULA_INCORRECT_VAR                = "Formel falsch. %s"
  val COMMENT_OPERATOR_MISSING_VAR                 = "Ein Operator %s fehlt."
  val COMMENT_RANGE_MISSING_VAR                    = "Der Bereich %s fehlt."
  val COMMENT_VALUE_CORRECT                        = "Wert richtig."
  val COMMENT_VALUE_INCORRECT_VAR                  = "Wert falsch. Erwartet wurde '%s'."
  val COMMENT_VALUE_MISSING                        = "Kein Wert angegeben."

  val ERROR_CLOSE_FILE       = "Beim Schließen des Dokuments ist ein Fehler aufgetreten."
  val ERROR_LOAD_CHART       = "Beim Öffnen eines Diagramms ist ein Fehler aufgetreten."
  val ERROR_LOAD_SAMPLE      = "Beim Laden der Musterdatei ist ein Fehler aufgetreten."
  val ERROR_LOAD_SHEET       = "Beim Laden eines Tabellenblatts ist ein Fehler aufgetreten."
  val ERROR_LOAD_SOLUTION    = "Beim Laden der eingereichten Datei ist ein Fehler aufgetreten."
  val ERROR_MISSING_SAMPLE   = "Die Musterdatei ist nicht vorhanden."
  val ERROR_MISSING_SOLUTION = "Die Lösungsdatei ist nicht vorhanden."
  val ERROR_WRONG_SHEET_NUM  = "Die Anzahl der Arbeitsblätter stimmt nicht überein. Haben Sie die richtige Datei hochgeladen?"

  val SUCCESS_CORRECTION = "Die Korrektur war erfolgreich."

}
