package initialData.web.coll_1

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.web.WebInitialExercise
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise
import model.{Exercise, FilesSolution, SampleSolution}

object WebColl1Ex1 extends WebInitialExercise(1, 1) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie eine passende h1 - Überschrift, die das Wort Autohersteller enthält.""",
      xpathQuery = "/html/body//h1",
      awaitedTagName = "h1",
      awaitedTextContent = Some("Autohersteller")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie eine ungeordnete Liste, die dann die einzelnen Hersteller enthalten wird.""",
      xpathQuery = "/html/body//ul",
      awaitedTagName = "ul"
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie ein Listenelement mit dem Text Audi.""",
      xpathQuery = """/html/body//ul/li[contains(text(), 'Audi')]""",
      awaitedTagName = "li",
      awaitedTextContent = Some("Audi")
    ),
    HtmlTask(
      id = 4,
      text = """Erstellen Sie ein Listenelement mit dem Text BMW.""",
      xpathQuery = """/html/body//ul/li[contains(text(), 'BMW')]""",
      awaitedTagName = "li",
      awaitedTextContent = Some("BMW")
    ),
    HtmlTask(
      id = 5,
      text = """Erstellen Sie ein Listenelement mit dem Text Mercedes.""",
      xpathQuery = """/html/body//ul/li[contains(text(), 'Mercedes')]""",
      awaitedTagName = "li",
      awaitedTextContent = Some("Mercedes")
    ),
    HtmlTask(
      id = 6,
      text = """Erstellen Sie ein Listenelement mit dem Text Volkswagen.""",
      xpathQuery = """/html/body//ul/li[contains(text(), 'Volkswagen')]""",
      awaitedTagName = "li",
      awaitedTextContent = Some("Volkswagen")
    ),
    HtmlTask(
      id = 7,
      text = """Binden Sie die vorgegebene Style - Datei carListStyle.css ein.
               |Die entsprechende Datei ist unter der URL carListStyle.css zu finden.
               |Setzen Sie auch den entsprechenden Wert für das Attribut rel!""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/head/link",
      awaitedTagName = "link",
      attributes = Map("href" -> "carListStyle.css", "rel" -> "stylesheet")
    )
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("carList.html", htmlFileType)
    )
  )

  private val sampleSolution: SampleSolution[FilesSolution] = SampleSolution(1, FilesSolution(sampleSolutionFiles))

  val webColl1Ex1: WebExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Listen in Html",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    content = WebExerciseContent(
      SiteSpec("carList.html", html_tasks, jsTasks = Seq.empty),
      loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("carList.html", htmlFileType, editable = true),
          FileLoadConfig("carListStyle.css", cssFileType)
        )
      ),
      Seq(sampleSolution)
    )
  )

}
