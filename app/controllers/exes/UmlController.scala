package controllers.exes

import java.nio.file.{Files, Path, Paths}
import javax.inject._

import com.fasterxml.jackson.databind.JsonNode
import controllers.core.AIdPartExController
import model.User
import model.core._
import model.core.result.CompleteResult
import model.uml.UmlEnums.UmlExPart
import model.uml._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.libs.Json
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object UmlController {

  val SolutionSchemaPath: Path = Paths.get("conf", "resources", "uml", "solutionSchema.json")

  val SolutionSchemaNode: JsonNode = Json.parse(String.join("\n", Files.readAllLines(SolutionSchemaPath)))

}

@Singleton
class UmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdPartExController[UmlExercise, UmlResult](cc, dbcp, r, UmlToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  // Yaml

  override type CompEx = UmlCompleteEx

  override val yamlFormat: YamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // db

  import profile.api._

  override type TQ = repo.UmlExerciseTable

  override def tq = repo.umlExercises

  override def completeExes: Future[Seq[UmlCompleteEx]] = db.run(repo.umlExercises.result).map(_.map(ex => UmlCompleteEx(ex)))

  override def completeExById(id: Int): Future[Option[UmlCompleteEx]] = db.run(repo.umlExercises.findBy(_.id).apply(id).result.headOption.map {
    case Some(ex) => Some(UmlCompleteEx(ex))
    case None     => None
  })

  override def saveRead(read: Seq[UmlCompleteEx]): Future[Seq[Int]] = Future.sequence(read.map(completeEx =>
    db.run(repo.umlExercises insertOrUpdate completeEx.ex)))

  // Views

  override def renderExercise(user: User, exercise: UmlCompleteEx, part: String): Future[Html] = Future(UmlExPart.valueOf(part) match {
    case UmlExPart.CLASS_SELECTION   => views.html.uml.classSelection.render(user, exercise.ex)
    case UmlExPart.DIAG_DRAWING      => views.html.uml.diagdrawing.render(user, exercise.ex, getsHelp = false)
    case UmlExPart.DIAG_DRAWING_HELP => views.html.uml.diagdrawing.render(user, exercise.ex, getsHelp = true)
    case UmlExPart.ATTRS_METHS       => views.html.uml.umlMatching.render(user, exercise.ex)
    case _                           => new Html("FEHLER!")
  })

  override val renderExesListRest = new Html(
    s"""<div class="alert alert-info">
       |Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte
       |der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(correctionResult: CompleteResult[UmlResult]): Html = ??? //FIXME

  // Correction

  override def correctEx(user: User, sol: StringSolution, exercise: UmlCompleteEx, part: String): Try[CompleteResult[UmlResult]] = {
    val umlSol: UmlSolution = null
    val (result, nextPart) = UmlExPart.valueOf(part) match {
      case UmlExPart.CLASS_SELECTION   => (ClassSelectionResult(exercise.ex, umlSol), UmlExPart.DIAG_DRAWING_HELP)
      case UmlExPart.DIAG_DRAWING_HELP => (DiagramDrawingHelpResult(exercise.ex, umlSol), UmlExPart.ATTRS_METHS)
      case UmlExPart.DIAG_DRAWING      => (DiagramDrawingResult(exercise.ex, umlSol), UmlExPart.FINISHED)
      case UmlExPart.ATTRS_METHS       => (null, UmlExPart.FINISHED)
      case UmlExPart.FINISHED          => (null, UmlExPart.FINISHED)
    }
    Try(new CompleteResult(sol.learnerSolution, List(result)))
  }

  // Other routes

  def checkSolution: EssentialAction = withAdmin { user =>
    implicit request => {
      //      val solNode = Json.parse(singleStrForm(StringConsts.SOLUTION_NAME).get.str)
      //      JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      //        case Success(_) => Ok("Ok...")
      //        case Failure(_) => BadRequest("FEHLER!")
      //      }
      Ok("TODO")
    }
  }

  def newExerciseStep2: EssentialAction = withAdmin { user =>
    implicit request =>
      //    exerciseReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
      //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
      //      case ReadingFailure(_) => BadRequest("There has been an error...")
      //      case ReadingResult(exercises) =>
      //        val exercise = exercises.head.read.asInstanceOf[UmlExercise]
      //        val parser = new UmlExTextParser(exercise.text, exercise.mappings.asScala.toMap, exercise.ignoreWords.asScala.toList)
      //        Ok(views.html.umlAdmin.newExerciseStep2Form.render(user, exercise, parser.capitalizedWords.toList))
      //    }
      Ok("TODO!")
  }

  def newExerciseStep3: EssentialAction = withAdmin { user =>
    implicit request =>
      //    exerciseReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
      //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
      //      case ReadingFailure(_) => BadRequest("There has been an error...")
      //      case ReadingResult(exercises) =>
      //        val exercise = exercises.head.read.asInstanceOf[UmlExercise]
      //        Ok(views.html.umlAdmin.newExerciseStep3Form.render(user, exercise))
      //    }
      Ok("TODO!")
  }

}
