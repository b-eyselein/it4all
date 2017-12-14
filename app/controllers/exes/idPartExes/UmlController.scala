package controllers.exes.idPartExes

import javax.inject._

import controllers.Secured
import model.core._
import model.uml.UmlConsts._
import model.uml.UmlEnums.UmlExPart._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlExPart, UmlMultiplicity}
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

  override type PartType = UmlExPart

  override def partTypeFromString(str: String): Option[UmlExPart] = UmlExPart.byString(str)

  override type SolType = UmlSolution

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[UmlSolution] = {
    // println(Solution.stringSolForm.bindFromRequest)
    Solution.stringSolForm.bindFromRequest.fold(_ => None, sol => readFromJson(Json.parse(sol.learnerSolution)))
  }

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[UmlSolution] = ???

  private def readFromJson(jsValue: JsValue): Option[UmlSolution] = jsValue.asObj flatMap { jsObj =>

    val maybeClasses = jsObj.arrayField(CLASSES_NAME, readClassFromJson)
    val maybeAssociations = jsObj.arrayField(ASSOCS_NAME, readAssociationFromJson)
    val maybeImplementations = jsObj.arrayField(IMPLS_NAME, readImplementationFromJson)

    (maybeClasses zip maybeAssociations zip maybeImplementations).headOption map {
      case ((classes, associations), implementations) => UmlSolution(classes, associations, implementations)
    }
  }

  private def readClassFromJson(jsValue: JsValue): Option[UmlCompleteClass] = jsValue.asObj flatMap { jsObj =>
    val maybeClassname = jsObj.stringField(NAME_NAME)
    val classType = jsObj.stringField(CLASSTYPE_NAME) flatMap UmlClassType.byString getOrElse UmlClassType.CLASS
    val maybeAttributes = jsObj.arrayField(ATTRS_NAME, readAttributeFromJson)
    val maybeMethods = jsObj.arrayField(METHODS_NAME, readMethodsFromJson)


    (maybeClassname zip maybeAttributes zip maybeMethods).headOption map {
      case ((name, attributes), methods) => UmlCompleteClass(UmlClass(-1, name, classType), attributes, methods)
    }
  }

  private def readAttributeFromJson(jsValue: JsValue): Option[UmlClassAttribute] = jsValue.asObj flatMap { jsObj =>

    val maybeName = jsObj.stringField(NAME_NAME)
    val maybeType = jsObj.stringField(TYPE_NAME)

    (maybeName zip maybeType).headOption map { case (name, attrType) => UmlClassAttribute(-1, "", name, attrType) }
  }

  private def readMethodsFromJson(jsValue: JsValue): Option[UmlClassMethod] = jsValue.asObj flatMap { jsObj =>
    val maybeName = jsObj.stringField(NAME_NAME)
    val maybeReturns = jsObj.stringField(ReturnTypeName)

    (maybeName zip maybeReturns).headOption map { case (name, returns) => UmlClassMethod(-1, "", name, returns) }
  }

  private def readAssociationFromJson(jsValue: JsValue): Option[UmlAssociation] = jsValue.asObj flatMap { jsObj =>

    val maybeAssocType = jsObj.stringField(ASSOCTYPE_NAME) flatMap UmlAssociationType.byString
    // TODO: assoc_name
    val maybeAssocName = jsObj.stringField(ASSOCNAME_NAME)
    val maybeFirstEnd = jsObj.stringField(FIRST_END_NAME)
    val maybeFirstMult = jsObj.stringField(FIRST_MULT_NAME) flatMap UmlMultiplicity.byString
    val maybeSecondEnd = jsObj.stringField(SECOND_END_NAME)
    val maybeSecondMult = jsObj.stringField(SECOND_MULT_NAME) flatMap UmlMultiplicity.byString

    (maybeAssocType zip maybeFirstEnd zip maybeFirstMult zip maybeSecondEnd zip maybeSecondMult).headOption map {
      case ((((assocType, firstEnd), firstMult), secondEnd), secondMult) => UmlAssociation(-1, assocType, maybeAssocName, firstEnd, firstMult, secondEnd, secondMult)
    }
  }

  private def readImplementationFromJson(jsValue: JsValue): Option[UmlImplementation] = jsValue.asObj flatMap { jsObj =>

    val maybeSubclass = jsObj.stringField(SUBCLASS_NAME)
    val maybeSuperClass = jsObj.stringField(SUPERCLASS_NAME)

    (maybeSubclass zip maybeSuperClass).headOption map { case (subClass, superClass) => UmlImplementation(-1, subClass, superClass) }
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

  override protected def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart): Future[Html] = Future(part match {
    case CLASS_SELECTION   => classSelection(user, exercise.ex)
    case DIAG_DRAWING      => diagdrawing(user, exercise, getsHelp = false)
    case DIAG_DRAWING_HELP => diagdrawing(user, exercise, getsHelp = true)
    case ALLOCATION        => allocation(user, exercise)
  })

  override protected val renderExesListRest = new Html(
    s"""<div class="alert alert-info">
       |  Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
       |</div>
       |<hr>""".stripMargin)

  override protected def renderResult(correctionResult: UmlResult): Html = umlResult(correctionResult)

  override protected def renderEditRest(exercise: Option[UmlCompleteEx]): Html = editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, part: UmlExPart): Try[UmlResult] = Try(part match {
    case CLASS_SELECTION   => ClassSelectionResult(exercise, sol)
    case DIAG_DRAWING_HELP => DiagramDrawingHelpResult(exercise, sol)
    case ALLOCATION        => AllocationResult(exercise, sol)

    case DIAG_DRAWING      =>
      // TODO: implement!
      println(sol)
      DiagramDrawingResult(exercise, sol)
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
          Ok("TODO: check solution" + language + solution.learnerSolution)
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