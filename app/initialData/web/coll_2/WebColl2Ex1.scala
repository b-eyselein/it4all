package initialData.web.coll_2

import de.uniwue.webtester.sitespec._
import initialData.InitialData._
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.{Exercise, ExerciseFile, FilesSolution, SampleSolution}

object WebColl2Ex1 {

  private val exResPath = exerciseResourcesPath("web", 2, 1)

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie einen Button mit dem Text 'Klick mich!'.
               |Beim Klick die Funktion 'increment()' aufgerufen werden.""".stripMargin.replace("\n", " "),
      xpathQuery = "/html/body//button",
      awaitedTagName = "button",
      attributes = Map("onclick" -> "increment()"),
      awaitedTextContent = Some("Klick mich!")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie einen Span mit der ID 'theSpan', in dem die Anzahl der Klicks angezeigt werden.
               |Zu Anfang soll dieser eine 0 anzeigen.""".stripMargin.replace("\n", " "),
      xpathQuery = """/html/body//span[@id='theSpan']""",
      awaitedTagName = "span",
      awaitedTextContent = Some("0")
    ),
    HtmlTask(
      id = 3,
      text = """Binden Sie die Javascript - Datei 'clickCounter.js' ein.""",
      xpathQuery = "/html/head//script",
      awaitedTagName = "script",
      attributes = Map("src" -> "clickCounter.js")
    )
  )

  private val js_tasks: Seq[JsTask] = (1 to 5).map { i =>
    JsTask(
      id = i,
      text = s"Test $i",
      preConditions = Seq(
        JsHtmlElementSpec(
          id = 1,
          xpathQuery = """/html/body//span[@id='theSpan']""",
          awaitedTagName = "span",
          awaitedTextContent = Some(s"${i - 1}")
        )
      ),
      action = JsAction(
        xpathQuery = "/html/body//button",
        actionType = JsActionType.Click,
        keysToSend = None
      ),
      postConditions = Seq(
        JsHtmlElementSpec(
          id = 1,
          xpathQuery = """/html/body//span[@id='theSpan']""",
          awaitedTagName = "span",
          awaitedTextContent = Some(s"$i")
        )
      )
    )
  }

  private val sampleSolution: SampleSolution[FilesSolution] = SampleSolution(
    id = 1,
    sample = FilesSolution(
      files = Seq(
        ExerciseFile(
          name = "clickCounter.html",
          fileType = "htmlmixed",
          editable = false,
          content = loadTextFromFile(exResPath / "sol_1" / "clickCounter.html")
        ),
        ExerciseFile(
          name = "clickCounter.js",
          fileType = "javascript",
          editable = false,
          content = loadTextFromFile(exResPath / "sol_1" / "clickCounter.js")
        )
      )
    )
  )

  val webColl2Ex1: WebExercise = Exercise(
    exerciseId = 1,
    collectionId = 2,
    toolId = "web",
    title = "Klickzähler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "clickCounter.html",
          fileType = "htmlmixed",
          editable = true,
          content = loadTextFromFile(exResPath / "clickCounter.html")
        ),
        ExerciseFile(
          name = "clickCounter.js",
          fileType = "javascript",
          editable = true,
          content = loadTextFromFile(exResPath / "clickCounter.js")
        )
      ),
      htmlText = Some("Erstellen Sie zunächst den Rumpf der Seite in HTML."),
      jsText = Some(
        """Implementieren Sie nun die Funktion <code>increment()</code> die aufgerufen wird wenn auf den Knopf
          |gedrückt wird.
          |Sie soll den Inhalt (innerHTML) des Elementes mit der id 'theSpan' auslesen und um 1 erhöhen.
          |Sie können die Funktion <code>parseInt(str)</code> verwenden um einen String in eine Ganzzahl
          |umzuwandeln.""".stripMargin.replace("\n", " ")
      ),
      siteSpec = SiteSpec(
        fileName = "clickCounter.html",
        htmlTasks = html_tasks,
        jsTasks = js_tasks
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )

}
