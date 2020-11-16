package initialData.web.coll_2

import de.uniwue.webtester.sitespec._
import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.web.WebInitialExercise
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.{Exercise, FilesSolution}

object WebColl2Ex3 extends WebInitialExercise(2, 3) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie ein Zahlen-Eingabefeld mit der ID 'number'.
               |Bei Änderung des Feldes (onchange) soll die Funktion 'fakultaet()' aufgerufen werden.""".stripMargin
        .replace("\n", " "),
      xpathQuery = """/html/body//input[@id='number']""",
      awaitedTagName = "input",
      attributes = Map("type" -> "number", "onchange" -> "fakultaet()")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie einen Span mit der ID 'result', in dem das Ergebnis der Berechnung stehen soll.
               |Zu Anfang soll dieser leer sein.""".stripMargin.replace("\n", " "),
      xpathQuery = """/html/body//span[@id='result']""",
      awaitedTagName = "span"
    ),
    HtmlTask(
      id = 3,
      text = """Binden Sie die Javascript-Datei 'factorial.js' ein.""",
      xpathQuery = "/html/head//script",
      awaitedTagName = "script",
      attributes = Map("src" -> "factorial.js")
    )
  )

  private val js_tasks: Seq[JsTask] = Seq(
    JsTask(
      id = 1,
      text = "Fakultät von 1 muss 1 sein.",
      preConditions = Seq.empty,
      action = JsAction(
        actionType = JsActionType.FillOut,
        xpathQuery = """/html/body//input[@id='number']""",
        keysToSend = Some("1")
      ),
      postConditions = Seq(
        JsHtmlElementSpec(
          id = 1,
          xpathQuery = """/html/body//span[@id='result']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("1")
        )
      )
    ),
    JsTask(
      id = 2,
      text = "Fakultät von 3 muss 6 sein.",
      preConditions = Seq.empty,
      action = JsAction(
        actionType = JsActionType.FillOut,
        xpathQuery = """/html/body//input[@id='number']""",
        keysToSend = Some("3")
      ),
      postConditions = Seq(
        JsHtmlElementSpec(
          id = 1,
          xpathQuery = """/html/body//span[@id='result']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("6")
        )
      )
    ),
    JsTask(
      id = 3,
      text = "Fakultät von 6 muss 720 sein.",
      preConditions = Seq.empty,
      action = JsAction(
        actionType = JsActionType.FillOut,
        xpathQuery = """/html/body//input[@id='number']""",
        keysToSend = Some("6")
      ),
      postConditions = Seq(
        JsHtmlElementSpec(
          id = 1,
          xpathQuery = """/html/body//span[@id='result']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("720")
        )
      )
    ),
    JsTask(
      id = 4,
      text = "Fakultät von 10 muss 3628800 sein.",
      preConditions = Seq.empty,
      action = JsAction(
        actionType = JsActionType.FillOut,
        xpathQuery = """/html/body//input[@id='number']""",
        keysToSend = Some("10")
      ),
      postConditions = Seq(
        JsHtmlElementSpec(
          id = 1,
          xpathQuery = """/html/body//span[@id='result']""",
          awaitedTagName = "span",
          awaitedTextContent = Some("3628800")
        )
      )
    )
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath / "sol_1",
    Seq(
      FileLoadConfig("factorial.html", htmlFileType),
      FileLoadConfig("factorial.js", jsFileType)
    )
  )

  val webColl2Ex3: WebExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Schleifen",
    authors = Seq("alg81dm"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    content = WebExerciseContent(
      siteSpec = SiteSpec("factorial.html", html_tasks, js_tasks),
      files = loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("factorial.html", htmlFileType, editable = true),
          FileLoadConfig("factorial.js", jsFileType, editable = true)
        )
      ),
      htmlText = Some("Erstellen Sie zunächst den Rumpf der Seite in HTML."),
      jsText = Some(
        """Implementieren Sie nun die Funktion <code>fakultaet()</code>, die bei Änderung des Felds aufgerufen wird.
          |Sie soll den Inhalt (value) des Eingabefeldes auslesen, die Fakultät davon berechnen und den
          |Inhalt (textContent) des Elements mit der ID 'result' auf das Ergebnis setzen.""".stripMargin
          .replace("\n", " ")
      ),
      sampleSolutions = Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
