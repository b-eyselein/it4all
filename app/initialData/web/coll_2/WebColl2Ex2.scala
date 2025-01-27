package initialData.web.coll_2

import initialData.InitialData._
import initialData.web.WebInitialExerciseContainer
import initialData.{FileLoadConfig, InitialExercise}
import model.tools.web.WebExerciseContent
import model.tools.web.sitespec._
import model.{FilesSolution, Level}

object WebColl2Ex2 extends WebInitialExerciseContainer(2, 2) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie ein Texteingabefeld mit der ID 'name'.""",
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//input[@id='name']""",
        awaitedTagName = "input",
        attributes = Map("type" -> "text")
      )
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie ein Passworteingabefeld mit der ID 'password'.
               |Bei Änderung des Passwortfeldes (onchange) soll die Funktion 'passwordStrength()' aufgerufen werden.""".stripMargin
        .replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//input[@id='password']""",
        awaitedTagName = "input",
        attributes = Map("type" -> "password", "onchange" -> "passwordStrength()")
      )
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie einen Span mit der ID 'errors', der später anzeigen soll, wenn das Passwort zu schwach ist.
               |Zu Anfang soll dieser leer sein.""".stripMargin.replace("\n", " "),
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//span[@id='errors']""",
        awaitedTagName = "span"
      )
    ),
    HtmlTask(
      id = 4,
      text = """Binden Sie die Javascript-Datei "pwChecker.js" ein.""",
      elementSpec = WebElementSpec(
        xpathQuery = """/html/head//script""",
        awaitedTagName = "script",
        attributes = Map("src" -> "pwChecker.js")
      )
    )
  )

  private val js_tasks: Seq[JsTask] = Seq(
    JsTask(
      id = 1,
      text = """Wenn das Passwort unter 8 Zeichen lang ist, setze den Fehlertext auf 'Zu kurz'.""",
      preConditions = Seq.empty,
      action = JsAction(
        xpathQuery = """/html/body//input[@id='password']""",
        actionType = JsActionType.FillOut,
        keysToSend = Some("123")
      ),
      postConditions = Seq(
        WebElementSpec(
          xpathQuery = """/html/body//span[@id='errors']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("Zu kurz")
        )
      )
    ),
    JsTask(
      id = 2,
      text = """Wenn das Passwort 'passwort' enthält, setze den Fehlertext auf 'Zu einfach'.""",
      preConditions = Seq.empty,
      action = JsAction(
        xpathQuery = """/html/body//input[@id='password']""",
        actionType = JsActionType.FillOut,
        keysToSend = Some("meinpasswort")
      ),
      postConditions = Seq(
        WebElementSpec(
          xpathQuery = """/html/body//span[@id='errors']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("Zu einfach")
        )
      )
    ),
    JsTask(
      id = 3,
      text = """Für alle anderen Passwörter soll das Fehlerfeld geleert (auf '' gesetzt) werden.""",
      preConditions = Seq.empty,
      action = JsAction(
        actionType = JsActionType.FillOut,
        xpathQuery = """/html/body//input[@id='password']""",
        keysToSend = Some("GanzGanzSicherUndGeheim")
      ),
      postConditions = Seq(
        WebElementSpec(
          xpathQuery = """/html/body//span[@id='errors']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("")
        )
      )
    )
  )

  val webColl2Ex2: InitialExercise[WebExerciseContent] = InitialExercise(
    title = "Verzweigungen und Strings",
    authors = Seq("alg81dm"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = Level.Advanced,
    content = WebExerciseContent(
      SiteSpec("pwChecker.html", html_tasks, js_tasks),
      loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("pwChecker.html", editable = true, Some("branchesStrings.html")),
          FileLoadConfig("pwChecker.js", editable = true, Some("branchesStrings.js"))
        )
      ),
      sampleSolutions = Seq(
        FilesSolution(
          loadFilesFromFolder(
            exResPath / "sol_1",
            Seq(
              FileLoadConfig("pwChecker.html", realFilename = Some("branchesStrings.html")),
              FileLoadConfig("pwChecker.js", realFilename = Some("branchesStrings.js"))
            )
          )
        )
      ),
      htmlText = Some("Erstellen Sie zunächst den Rumpf der Seite in HTML."),
      jsText = Some(
        """Implementieren Sie nun die Funktion <code>passwordStrength()</code>, die bei Änderung des Felds aufgerufen
          |wird.
          |Sie soll den Inhalt (value) des Passwortfeldes auslesen und verschiedene Tests durchführen.
          |Bei fehlgeschlagenen Tests soll der Inhalt des Elements mit der ID 'errors' auf den entsprechenden Text
          |gesetzt werden.
          |Es soll immer nur der erste fehlgeschlagene Test (in der Reihenfolge der Teilaufgaben) beachtet werden.
          |Wenn kein Test fehlschlägt, soll der Fehlertext gelöscht werden (auf '' setzen).
          |Wenn das Passwort unter 8 Zeichen lang ist, soll der Fehlertext 'Zu kurz' lauten.
          |Wenn die Eingabe den Teilstring 'passwort' enthält, soll 'Zu einfach' gesetzt werden.""".stripMargin
          .replace("\n", " ")
      )
    )
  )

}
