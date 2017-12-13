package controllers.exes.idPartExes

import javax.inject._

import controllers.Secured
import model.core._
import model.uml.UmlConsts._
import model.uml.UmlEnums.UmlExPart._
import model.uml.UmlEnums.{UmlClassType, UmlExPart}
import model.uml._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, ControllerComponents, EssentialAction, Request}
import play.twirl.api.Html
import views.html.uml._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class UmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdPartExController[UmlExercise, EvaluationResult, UmlResult](cc, dbcp, r, UmlToolObject) with JsonFormat with Secured {

  override type SolType = UmlSolution

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[UmlSolution] =
    Solution.stringSolForm.bindFromRequest.fold(_ => None, sol => readFromJson(Json.parse(sol.learnerSolution)))

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[UmlSolution] = ???

  private def readFromJson(jsValue: JsValue): Option[UmlSolution] = jsValue.asObj flatMap { jsObj =>
    val maybeClasses = jsObj.arrayField(CLASSES_NAME, readClassFromJson)
    maybeClasses map { classes =>
      UmlSolution(
        classes,
        associations = Seq.empty,
        implementations = Seq.empty)
    }
  }

  private def readClassFromJson(jsValue: JsValue): Option[UmlCompleteClass] = jsValue.asObj map { jsObj =>
    UmlCompleteClass(
      clazz = UmlClass(exerciseId = -1, className = jsObj.stringField(NAME_NAME) getOrElse "", classType = UmlClassType.CLASS),
      attributes = Seq.empty,
      methods = Seq.empty)
  }

  // Yaml

  override type CompEx = UmlCompleteEx

  override val yamlFormat: YamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // db

  override type TQ = repo.UmlExercisesTable

  override def tq: repo.ExerciseTableQuery[UmlExercise, UmlCompleteEx, repo.UmlExercisesTable] = repo.umlExercises

  override def futureCompleteExes: Future[Seq[UmlCompleteEx]] = repo.umlExercises.completeExes

  override def futureCompleteExById(id: Int): Future[Option[UmlCompleteEx]] = repo.umlExercises.completeById(id)

  override def saveRead(read: Seq[UmlCompleteEx]): Future[Seq[Boolean]] = Future.sequence(read map repo.saveCompleteEx)

  // Views

  override protected def renderExercise(user: User, exercise: UmlCompleteEx, part: String): Future[Html] = Future(UmlExPart.valueOf(part) match {
    case CLASS_SELECTION   => classSelection(user, exercise.ex)
    case DIAG_DRAWING      => diagdrawing(user, exercise, getsHelp = false)
    case DIAG_DRAWING_HELP => diagdrawing(user, exercise, getsHelp = true)
    case ATTRS_METHS       => umlMatching(user, exercise)
    case _                 => new Html("FEHLER!")
  })

  override protected val renderExesListRest = new Html(
    s"""<div class="alert alert-info">
       |  Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
       |</div>
       |<hr>""".stripMargin)

  override protected def renderResult(correctionResult: UmlResult): Html = umlResult(correctionResult)

  override protected def renderEditRest(exercise: Option[UmlCompleteEx]): Html = editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, part: String): Try[UmlResult] = Try({
    UmlExPart.valueOf(part) match {
      case CLASS_SELECTION          => ClassSelectionResult(exercise, sol)
      case (DIAG_DRAWING_HELP)      =>
        println(sol)
        DiagramDrawingHelpResult(exercise, sol)
      case DIAG_DRAWING             =>
        println(sol)
        DiagramDrawingResult(exercise, sol)
      case (ATTRS_METHS | FINISHED) => ???
    }
  })

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
      //        Ok(views.html.umlAdmin.newExerciseStep2Form(user, exercise, parser.capitalizedWords.toList))
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
      //        Ok(views.html.umlAdmin.newExerciseStep3Form(user, exercise))
      //    }
      Ok("TODO!")
  }

  def activityExercise: EssentialAction = withAdmin { user =>
    implicit request => Ok(views.html.umlActivity.activitiyDrawing(user))
  }

  // FIXME: used where?
  def activityCheckSolution(language: String): EssentialAction = withAdmin { user =>
    implicit request => {
      Solution.stringSolForm.bindFromRequest.fold(_ => BadRequest("TODO!"),
        solution => {
          println("Checking Solution: " + language)
          println(solution)
          Ok("TODO_checksolution")
        }
      )

      //      val solNode = Json.parse(singleStrForm(StringConsts.SOLUTION_NAME).get.str)
      //      JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      //        case Success(_) => Ok("Ok...")
      //        case Failure(_) => BadRequest("FEHLER!")
      //      }
    }
  }

}