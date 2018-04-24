package model.xml

import java.nio.file._

import controllers.ExerciseOptions
import javax.inject._
import model.Enums.{ExerciseState, ToolState}
import model.core._
import model.toolMains.IdExerciseToolMain
import model.xml.XmlConsts._
import model.yaml.MyYamlFormat
import model.{Consts, Enums, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.{Html, HtmlFormat}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}


@Singleton
class XmlToolMain @Inject()(val tables: XmlTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("xml") with FileUtils {

  // Result types

  override type ExType = XmlExercise

  override type CompExType = XmlExercise

  override type Tables = XmlTableDefs

  override type PartType = XmlExPart

  override type SolType = XmlSolution

  override type R = XmlError

  override type CompResult = XmlCompleteResult

  // Other members

  override val toolname: String = "Xml"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = XmlConsts

  override val exParts: Seq[XmlExPart] = XmlExParts.values

  override implicit val compExForm: Form[XmlExercise] = Form(mapping(
    "id" -> number,
    "title" -> nonEmptyText,
    "author" -> nonEmptyText,
    "text" -> nonEmptyText,
    "status" -> of[ExerciseState],
    "grammarDescription" -> nonEmptyText,
    "sampleGrammar" -> nonEmptyText,
    "rootNode" -> nonEmptyText
  )(XmlExercise.apply)(XmlExercise.unapply))

  // Reading solution from requests, saving

  override def readSolutionFromPostRequest(user: User, id: Int, part: XmlExPart)(implicit request: Request[AnyContent]): Option[XmlSolution] =
    SolutionFormHelper.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution)) map { sol => XmlSolution(user.username, id, part, sol) }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: XmlExPart): Option[XmlSolution] =
    jsValue.asStr map (XmlSolution(user.username, id, part, _))

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): XmlExercise =
    XmlExercise(id, title = "", author = "", text = "", state, grammarDescription = "", sampleGrammar = "", rootNode = "")

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // Correction

  override protected def correctEx(user: User, solution: XmlSolution, completeEx: XmlExercise, solutionSaved: Boolean): Future[Try[XmlCompleteResult]] =
    Future(solution.part match {
      case DocumentCreationXmlPart =>
        checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

          val grammarAndXmlTries: Try[(Path, Path)] = for {
            grammar <- write(dir, completeEx.rootNode + ".dtd", completeEx.sampleGrammar)
            xml <- write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, solution.solution)
          } yield (grammar, xml)

          grammarAndXmlTries map { case (grammar, xml) =>
            val correctionResult = XmlCorrector.correctAgainstMentionedDTD(xml)
            XmlCompleteResult(solution.solution, solutionSaved, correctionResult)
          }
        })

      case GrammarCreationXmlPart =>
        XmlCorrector.correctDTD(solution.solution, completeEx)
        Failure(new Exception("Not implemented yet: correction of grammar..."))
    })

  // Views

  override def renderExerciseEditForm(user: User, newEx: XmlExercise, isCreation: Boolean): Html =
    views.html.idExercises.xml.editXmlExercise(user, this, newEx, isCreation)

  override def renderExercise(user: User, exercise: XmlExercise, part: XmlExPart, maybeOldSolution: Option[XmlSolution]): Html = {
    val template = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    val exScript: Html = Html(s"""<script src="${controllers.routes.Assets.versioned("javascripts/xml/xmlExercise.js").url}"></script>""")

    val exRest = part match {
      case DocumentCreationXmlPart => Html(s"""<pre>${HtmlFormat.escape(exercise.sampleGrammar)}</pre>""")
      case GrammarCreationXmlPart  => Html(s"""<div class="well">${exercise.grammarDescription}</div>""")
    }

    views.html.core.exercise2Rows(user, this, ExerciseOptions("xml", 15, 30), exercise.ex, exRest, template, part, exScript)
  }

  // Result handlers

  override def onSubmitCorrectionResult(user: User, result: XmlCompleteResult): Html =
    views.html.core.correction.render(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: XmlCompleteResult): JsValue = result.toJson

}