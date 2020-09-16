package initialData.web.coll_2

import de.uniwue.webtester.sitespec._
import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.web.WebInitialExercise
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.{Exercise, FilesSolution, SampleSolution}

object WebColl2Ex1 extends WebInitialExercise(2, 1) {

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

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath / "sol_1",
    Seq(
      FileLoadConfig("clickCounter.html", htmlFileType),
      FileLoadConfig("clickCounter.js", jsFileType)
    )
  )

  private val sampleSolution: SampleSolution[FilesSolution] = SampleSolution(1, FilesSolution(sampleSolutionFiles))

  val webColl2Ex1: WebExercise = Exercise(
    exerciseId,
    collectionId,
    toolId = "web",
    title = "Klickzähler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    content = WebExerciseContent(
      SiteSpec("clickCounter.html", html_tasks, js_tasks),
      loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("clickCounter.html", htmlFileType, editable = true),
          FileLoadConfig("clickCounter.js", jsFileType, editable = true)
        )
      ),
      Seq(sampleSolution),
      htmlText = Some("Erstellen Sie zunächst den Rumpf der Seite in HTML."),
      jsText = Some(
        """Implementieren Sie nun die Funktion <code>increment()</code> die aufgerufen wird wenn auf den Knopf
          |gedrückt wird.
          |Sie soll den Inhalt (innerHTML) des Elementes mit der id 'theSpan' auslesen und um 1 erhöhen.
          |Sie können die Funktion <code>parseInt(str)</code> verwenden um einen String in eine Ganzzahl
          |umzuwandeln.""".stripMargin.replace("\n", " ")
      )
    )
  )

}
