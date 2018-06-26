package model.xml

import java.nio.file._

import javax.inject._
import model.core._
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, User}
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class XmlToolMain @Inject()(val tables: XmlTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("xml") with FileUtils {

  // Result types

  override type ExType = XmlExercise

  override type CompExType = XmlExercise

  override type Tables = XmlTableDefs

  override type PartType = XmlExPart

  override type SolType = XmlSolution

  override type R = XmlEvaluationResult

  override type CompResult = XmlCompleteResult

  // Other members

  override val hasPlayground = true

  override val toolname: String = "Xml"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = XmlConsts

  override val exParts: Seq[XmlExPart] = XmlExParts.values

  override implicit val compExForm: Form[XmlExercise] = null
  //    Form(mapping(
  //    "id" -> number,
  //    "title" -> nonEmptyText,
  //    "author" -> nonEmptyText,
  //    "text" -> nonEmptyText,
  //    "status" -> ExerciseState.formField,
  //    "grammarDescription" -> nonEmptyText,
  //    "sampleGrammar" -> nonEmptyText,
  //    "rootNode" -> nonEmptyText
  //  )(XmlExercise.apply)(XmlExercise.unapply))

  // Reading solution from requests, saving

  override def readSolutionFromPostRequest(user: User, id: Int, part: XmlExPart)(implicit request: Request[AnyContent]): Option[XmlSolution] =
    SolutionFormHelper.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution)) map { sol => XmlSolution(user.username, id, part, sol) }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: XmlExPart): Option[XmlSolution] =
    jsValue.asStr map (XmlSolution(user.username, id, part, _))

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): XmlExercise =
    XmlExercise(id, title = "", author = "", text = "", state, grammarDescription = "", sampleGrammar = null, rootNode = "")

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // Correction

  override protected def correctEx(user: User, solution: XmlSolution, completeEx: XmlExercise, solutionSaved: Boolean): Future[Try[CompResult]] =
    Future(solution.part match {
      case XmlExParts.DocumentCreationXmlPart => checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

        val grammarAndXmlTries: Try[(Path, Path)] = for {
          grammar <- write(dir, completeEx.rootNode + ".dtd", completeEx.sampleGrammar.asString)
          xml <- write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, solution.solution)
        } yield (grammar, xml)

        grammarAndXmlTries map { case (grammar, xml) =>
          val correctionResult = XmlCorrector.correctAgainstMentionedDTD(xml)
          XmlDocumentCompleteResult(solution.solution, solutionSaved, correctionResult)
        }
      })

      case XmlExParts.GrammarCreationXmlPart => DocTypeDefParser.parseDTD(solution.solution) map { userGrammar =>
        XmlGrammarCompleteResult(userGrammar, solutionSaved, completeEx)
      }
    })

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: XmlExPart): Future[String] = ???

  // Views

  override def renderExerciseEditForm(user: User, newEx: XmlExercise, isCreation: Boolean): Html =
    views.html.idExercises.xml.editXmlExercise(user, this, newEx, isCreation)

  override def renderExercise(user: User, exercise: XmlExercise, part: XmlExPart, maybeOldSolution: Option[XmlSolution]): Html = {
    val oldSolutionOrTemplate = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    views.html.idExercises.xml.xmlExercise(user, this, exercise.ex, oldSolutionOrTemplate, part)
  }

  override def playground(user: User): Html = views.html.idExercises.xml.xmlPlayground(user)

  // Result handlers

  override def onLiveCorrectionResult(pointsSaved: Boolean, result: CompResult): JsValue = result.toJson

}