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

  override type CompExType = XmlCompleteExercise

  override type Tables = XmlTableDefs

  override type PartType = XmlExPart

  override type SolType = String

  override type DBSolType = XmlSolution

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

  override def readSolutionFromPostRequest(user: User, id: Int, part: XmlExPart)(implicit request: Request[AnyContent]): Option[SolType] =
    SolutionFormHelper.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: XmlExPart): Option[SolType] = jsValue.asStr

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): XmlCompleteExercise = XmlCompleteExercise(
    XmlExercise(id, title = "", author = "", text = "", state, grammarDescription = "", rootNode = ""),
    Seq.empty)

  override def instantiateSolution(username: String, exerciseId: Int, part: XmlExPart, solution: String, points: Double, maxPoints: Double): XmlSolution =
    XmlSolution(username, exerciseId, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlCompleteExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // Correction

  override protected def correctEx(user: User, solution: SolType, completeEx: XmlCompleteExercise, part: XmlExPart): Future[Try[CompResult]] =
    Future(part match {
      case XmlExParts.DocumentCreationXmlPart => checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

        val grammarAndXmlTries: Try[(Path, Path)] = for {
          grammar <- write(dir, completeEx.ex.rootNode + ".dtd", completeEx.sampleGrammars.head.sampleGrammar.asString)
          xml <- write(dir, completeEx.ex.rootNode + "." + XML_FILE_ENDING, solution)
        } yield (grammar, xml)

        grammarAndXmlTries map { case (grammar, xml) =>
          val correctionResult = XmlCorrector.correctAgainstMentionedDTD(xml)
          XmlDocumentCompleteResult(solution, correctionResult)
        }
      })

      case XmlExParts.GrammarCreationXmlPart => DocTypeDefParser.parseDTD(solution) map { userGrammar =>
        XmlGrammarCompleteResult(userGrammar, completeEx)
      }
    })

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: XmlExPart): Future[String] = ???

  // Views

  override def renderExerciseEditForm(user: User, newEx: XmlCompleteExercise, isCreation: Boolean): Html =
    views.html.idExercises.xml.editXmlExercise(user, this, newEx, isCreation)

  override def renderExercise(user: User, exercise: XmlCompleteExercise, part: XmlExPart, maybeOldSolution: Option[XmlSolution]): Html = {
    val oldSolutionOrTemplate = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    views.html.idExercises.xml.xmlExercise(user, this, exercise, oldSolutionOrTemplate, part)
  }

  override def playground(user: User): Html = views.html.idExercises.xml.xmlPlayground(user)

}