package initialData.web.coll_1

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import initialData.InitialData.{ex_resources_path, load_text_from_file}
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}
import model.{Exercise, ExerciseFile, SampleSolution}

object WebColl1Ex1 {

  private val ex_res_folder = ex_resources_path("web", 1, 1)

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

  private val sampleSolution: SampleSolution[WebSolution] = SampleSolution(
    id = 1,
    sample = WebSolution(
      files = Seq(
        ExerciseFile(
          name = "carList.html",
          fileType = "htmlmixed",
          editable = false,
          content = load_text_from_file(ex_res_folder / "sol_1" / "carList.html")
        )
      )
    )
  )

  val webColl1Ex1: WebExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "web",
    title = "Listen in Html",
    authors = Seq("bje40dc"),
    text = load_text_from_file(ex_res_folder / "text.html"),
    difficulty = 1,
    topicAbbreviations = Seq(),
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "carList.html",
          fileType = "htmlmixed",
          editable = true,
          content = load_text_from_file(ex_res_folder / "carList.html")
        ),
        ExerciseFile(
          name = "carListStyle.css",
          fileType = "css",
          editable = false,
          content = load_text_from_file(ex_res_folder / "carListStyle.css")
        )
      ),
      siteSpec = SiteSpec(
        fileName = "carList.html",
        htmlTasks = html_tasks,
        jsTasks = Seq.empty
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )

}
