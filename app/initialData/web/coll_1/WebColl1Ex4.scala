package initialData.web.coll_1

import initialData.InitialData._
import initialData.web.WebInitialExerciseContainer
import initialData.{FileLoadConfig, InitialExercise}
import model.tools.web.WebExerciseContent
import model.tools.web.sitespec.{HtmlTask, SiteSpec, WebElementSpec}
import model.{FilesSolution, Level}

object WebColl1Ex4 extends WebInitialExerciseContainer(1, 4) {

  private val html_tasks: Seq[HtmlTask] = Seq(
    HtmlTask(
      id = 1,
      text = """Erstellen Sie das Formular - Tag.
               |Geben Sie für die Attribute 'action' und 'method' jeweils die Werte '/test.php' und 'post' an.""".stripMargin
        .replace('\n', ' '),
      elementSpec = WebElementSpec(
        xpathQuery = "/html/body//form",
        awaitedTagName = "form",
        attributes = Map("action" -> "/test.php", "method" -> "post")
      )
    ),
    HtmlTask(
      id = 2,
      text = """Erstellen Sie ein Label mit dem Inhalt 'Email' für das Eingabefeld mit der ID 'email'.""",
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//form//label[@for ='email']""",
        awaitedTagName = "label",
        awaitedTextContent = Some("Email")
      )
    ),
    HtmlTask(
      id = 3,
      text = """Erstellen Sie ein Feld zur Eingabe der Emailadresse.
               |Benutzen Sie den in der Vorlesung gezeigten Typen und geben Sie den Attributen 'name' und 'id' jeweils den
               |Wert 'email'.
               |Die Eingabe soll eines Wertes soll außerdem verpflichtend sein.""".stripMargin.replace('\n', ' '),
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//form//input[@type='email']""",
        awaitedTagName = "input",
        attributes = Map("name" -> "email", "id" -> "email", "required" -> "true")
      )
    ),
    HtmlTask(
      id = 4,
      text = """Erstellen Sie ein Label mit dem Inhalt 'Passwort' für das Eingabefeld mit der ID 'passwort'.""",
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//form//label[@for='passwort']""",
        awaitedTagName = "label",
        awaitedTextContent = Some("Passwort")
      )
    ),
    HtmlTask(
      id = 5,
      text = """Erstellen Sie ein Feld zur Eingabe des Passworts.Benutzen Sie den in der Vorlesung gezeigten Typen und
               |geben Sie dem Attribut 'name' den Wert 'passwort'.Die Eingabe soll außerdem verpflichtend sein.""".stripMargin
        .replace('\n', ' '),
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//form//input[@type='password']""",
        awaitedTagName = "input",
        attributes = Map("name" -> "passwort", "id" -> "passwort", "required" -> "true")
      )
    ),
    HtmlTask(
      id = 6,
      text = """Erstellen Sie einen Knopf ('input', kein 'button') um das Formular abzusenden.""",
      elementSpec = WebElementSpec(
        xpathQuery = """/html/body//form//input[@type='submit']""",
        awaitedTagName = "input"
      )
    ),
    HtmlTask(
      id = 7,
      text = """Binden Sie die vorgegebene CSS - Datei 'loginStyle.css' ein.
               |Die entsprechende Datei ist unter der URL 'loginStyle.css' zu finden.
               |Setzen Sie auch den entsprechenden Wert für das Attribut'rel'.""".stripMargin
        .replace('\n', ' '),
      elementSpec = WebElementSpec(
        xpathQuery = "/html/head//link",
        awaitedTagName = "link",
        attributes = Map("rel" -> "stylesheet", "href" -> "loginStyle.css")
      )
    )
  )

  val webColl1Ex4: InitialExercise[WebExerciseContent] = InitialExercise(
    title = "Login-Formular",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = Level.Advanced,
    content = WebExerciseContent(
      SiteSpec("login.html", html_tasks, jsTasks = Seq.empty),
      files = loadFilesFromFolder(
        exResPath,
        Seq(
          FileLoadConfig("login.html", editable = true),
          FileLoadConfig("loginStyle.css")
        )
      ),
      sampleSolutions = Seq(
        FilesSolution(
          loadFilesFromFolder(
            exResPath / "sol_1",
            Seq(FileLoadConfig("login.html"))
          )
        )
      )
    )
  )

}
