package initialData.web.coll_1

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import initialData.InitialData.{ex_resources_path, load_text_from_file}
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}
import model.{Exercise, ExerciseFile, SampleSolution}

object WebColl1Ex4 {

  private val ex_res_path = ex_resources_path("web", 1, 4)

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie das Formular - Tag.
               |Geben Sie für die Attribute 'action' und 'method' jeweils die Werte '/test.php' und 'post' an.""".stripMargin
        .replace('\n', ' '),
      xpathQuery = "/html/body//form",
      awaitedTagName = "form",
      attributes = Map("action" -> "/test.php", "method" -> "post")
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie ein Label mit dem Inhalt 'Email' für das Eingabefeld mit der ID 'email'.""",
      xpathQuery = """/html/body//form//label[@for ='email']""",
      awaitedTagName = "label",
      awaitedTextContent = Some("Email")
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie ein Feld zur Eingabe der Emailadresse.
               |Benutzen Sie den in der Vorlesung gezeigten Typen und geben Sie den Attributen 'name' und 'id' jeweils den
               |Wert 'email'.
               |Die Eingabe soll eines Wertes soll außerdem verpflichtend sein.""".stripMargin.replace('\n', ' '),
      xpathQuery = """/html/body//form//input[@type='email']""",
      awaitedTagName = "input",
      attributes = Map("name" -> "email", "id" -> "email", "required" -> "true")
    ),
    HtmlTask(
      id = 4,
      text = """Erstellen Sie ein Label mit dem Inhalt 'Passwort' für das Eingabefeld mit der ID 'passwort'.""",
      xpathQuery = """/html/body//form//label[@for='passwort']""",
      awaitedTagName = "label",
      awaitedTextContent = Some("Passwort")
    ),
    HtmlTask(
      id = 5,
      text = """Erstellen Sie ein Feld zur Eingabe des Passworts.Benutzen Sie den in der Vorlesung gezeigten Typen und
               |geben Sie dem Attribut 'name' den Wert 'passwort'.Die Eingabe soll außerdem verpflichtend sein.""".stripMargin
        .replace('\n', ' '),
      xpathQuery = """/html/body//form//input[@type='password']""",
      awaitedTagName = "input",
      attributes = Map("name" -> "passwort", "id" -> "passwort", "required" -> "true")
    ),
    HtmlTask(
      id = 6,
      text = """Erstellen Sie einen Knopf ('input', kein 'button') um das Formular abzusenden.""",
      xpathQuery = """/html/body//form//input[@type='submit']""",
      awaitedTagName = "input"
    ),
    HtmlTask(
      id = 7,
      text = """Binden Sie die vorgegebene CSS - Datei 'loginStyle.css' ein.
               |Die entsprechende Datei ist unter der URL 'loginStyle.css' zu finden.
               |Setzen Sie auch den entsprechenden Wert für das Attribut'rel'.""".stripMargin
        .replace('\n', ' '),
      xpathQuery = "/html/head//link",
      awaitedTagName = "link",
      attributes = Map("rel" -> "stylesheet", "href" -> "loginStyle.css")
    )
  )

  private val sampleSolution: SampleSolution[WebSolution] = SampleSolution(
    id = 1,
    sample = WebSolution(
      files = Seq(
        ExerciseFile(
          name = "login.html",
          fileType = "htmlmixed",
          editable = false,
          content = load_text_from_file(ex_res_path / "sol_1" / "login.html")
        )
      )
    )
  )

  val webColl1Ex4: WebExercise = Exercise(
    exerciseId = 4,
    collectionId = 1,
    toolId = "web",
    title = "Login-Formular",
    authors = Seq("bje40dc"),
    text = load_text_from_file(ex_res_path / "text.html"),
    difficulty = 3,
    topicAbbreviations = Seq.empty,
    content = WebExerciseContent(
      files = Seq(
        ExerciseFile(
          name = "login.html",
          fileType = "htmlmixed",
          editable = true,
          content = load_text_from_file(ex_res_path / "login.html")
        ),
        ExerciseFile(
          name = "loginStyle.css",
          fileType = "css",
          editable = false,
          content = load_text_from_file(ex_res_path / "loginStyle.css")
        )
      ),
      siteSpec = SiteSpec(
        fileName = "login.html",
        htmlTasks = html_tasks,
        jsTasks = Seq.empty
      ),
      sampleSolutions = Seq(sampleSolution)
    )
  )

}
