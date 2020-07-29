package initialData.web.coll_2

import de.uniwue.webtester.sitespec._
import initialData.InitialData._
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}
import model.{Exercise, ExerciseFile, SampleSolution}

object WebColl2Ex3 {

  private val exResPath = exerciseResourcesPath("web", 2, 3)

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

  private val sampleSolution: SampleSolution[WebSolution] = SampleSolution(
    id = 1,
    sample = WebSolution(
      files = Seq(
        ExerciseFile(
          name = "factorial.html",
          fileType = "htmlmixed",
          editable = false,
          content = loadTextFromFile(exResPath / "sol_1" / "factorial.html")
        ),
        ExerciseFile(
          name = "factorial.js",
          fileType = "javascript",
          editable = false,
          content = loadTextFromFile(exResPath / "sol_1" / "factorial.js")
        )
      )
    )
  )

  val webColl2Ex3: WebExercise = Exercise(
    exerciseId = 3,
    collectionId = 2,
    toolId = "web",
    title = "Schleifen",
    authors = Seq("alg81dm"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "factorial.html",
          fileType = "htmlmixed",
          editable = true,
          content = loadTextFromFile(exResPath / "factorial.html")
        ),
        ExerciseFile(
          name = "factorial.js",
          fileType = "javascript",
          editable = true,
          content = loadTextFromFile(exResPath / "factorial.js")
        )
      ),
      htmlText = Some("Erstellen Sie zunächst den Rumpf der Seite in HTML."),
      jsText = Some(
        """Implementieren Sie nun die Funktion <code>fakultaet()</code>, die bei Änderung des Felds aufgerufen wird.
          |Sie soll den Inhalt (value) des Eingabefeldes auslesen, die Fakultät davon berechnen und den
          |Inhalt (textContent) des Elements mit der ID 'result' auf das Ergebnis setzen.""".stripMargin
          .replace("\n", " ")
      ),
      siteSpec = SiteSpec(
        fileName = "factorial.html",
        htmlTasks = html_tasks,
        jsTasks = js_tasks
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )

}
