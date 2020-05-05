package initialData.web.coll_1

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import initialData.InitialData.{ex_resources_path, load_text_from_file}
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}
import model.{Exercise, ExerciseFile, SampleSolution}

object WebColl1Ex5 {

  private val ex_res_path = ex_resources_path("web", 1, 5)

  private val sampleSolution = SampleSolution(
    id = 1,
    sample = WebSolution(
      files = Seq(
        ExerciseFile(
          name = "audio.html",
          fileType = "htmlmixed",
          editable = false,
          content = load_text_from_file(ex_res_path / "sol_1" / "audio.html")
        )
      )
    )
  )

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie eine passende h1-Überschrift die 'Deutsche Nationalhymne' enthält.""",
      xpathQuery = "/html/body//h1",
      awaitedTagName = "h1",
      awaitedTextContent = Some("Deutsche Nationalhymne")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie das Grundelement für die Audiodatei und aktivieren Sie die Kontrollelement.
               |Falls der Browser keine Audiodateien unterstützt, soll der Text 'Ihr Browser unterstützt kein Audio!'
               |ausgegeben werden.""".stripMargin
        .replace("\n", " "),
      xpathQuery = "/html/body//audio",
      awaitedTagName = "audio",
      attributes = Map("controls" -> "true"),
      awaitedTextContent = Some("Ihr Browser unterstützt kein Audio!")
    ),
    HtmlTask(
      id = 3,
      text =
        """Erstellen Sie das Element für die Quelldatei. Diese ist vom Typ 'audio/ogg' und befindet sich an der URL
          |'https=//upload.wikimedia.org/wikipedia/commons/c/cb/National_anthem_of_Germany_-_U.S._Army_1st_Armored_Division_Band.ogg'""".stripMargin
          .replace("\n", " "),
      xpathQuery = "/html/body//audio/source",
      awaitedTagName = "source",
      attributes = Map(
        "type" -> "audio/ogg",
        "src"  -> "https=//upload.wikimedia.org/wikipedia/commons/c/cb/National_anthem_of_Germany_-_U.S._Army_1st_Armored_Division_Band.ogg"
      )
    )
  )

  val webColl1Ex5: WebExercise = Exercise(
    exerciseId = 5,
    collectionId = 1,
    toolId = "web",
    title = "Audio in HTML 5",
    authors = Seq("bje40dc"),
    text = load_text_from_file(ex_res_path / "text.html"),
    difficulty = 1,
    topicAbbreviations = Seq.empty,
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "audio.html",
          fileType = "htmlmixed",
          editable = true,
          content = load_text_from_file(ex_res_path / "audio.html")
        )
      ),
      siteSpec = SiteSpec(
        fileName = "audio.html",
        htmlTasks = html_tasks,
        jsTasks = Seq.empty
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )
}
