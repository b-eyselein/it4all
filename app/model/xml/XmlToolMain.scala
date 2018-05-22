package model.xml

import java.nio.file._

import controllers.ExerciseOptions
import javax.inject._
import model.core._
import model.core.matching.MatchingResult
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.xml.XmlConsts._
import model.xml.dtd.ElementLine
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, User}
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.{Html, HtmlFormat}

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
      case DocumentCreationXmlPart =>
        checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

          val grammarAndXmlTries: Try[(Path, Path)] = for {
            grammar <- write(dir, completeEx.rootNode + ".dtd", completeEx.sampleGrammar.asString)
            xml <- write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, solution.solution)
          } yield (grammar, xml)

          grammarAndXmlTries map { case (grammar, xml) =>
            val correctionResult = XmlCorrector.correctAgainstMentionedDTD(xml)
            XmlDocumentCompleteResult(solution.solution, solutionSaved, correctionResult)
          }
        })

      case GrammarCreationXmlPart =>
        val results: Try[MatchingResult[ElementLine, ElementLineMatch]] = XmlCorrector.correctDTD(solution.solution, completeEx)

        results map (matches => XmlGrammarCompleteResult(solution.solution, solutionSaved, matches))
    })

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: XmlExPart): Future[String] = ???

  // Views

  override def renderExerciseEditForm(user: User, newEx: XmlExercise, isCreation: Boolean): Html =
    views.html.idExercises.xml.editXmlExercise(user, this, newEx, isCreation)

  override def renderExercise(user: User, exercise: XmlExercise, part: XmlExPart, maybeOldSolution: Option[XmlSolution]): Html = {
    val template = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    val exScript: Html = Html(s"""<script src="${controllers.routes.Assets.versioned("javascripts/xml/xmlExercise.js").url}"></script>""")

    val exRest = part match {
      case DocumentCreationXmlPart => Html(s"""<pre>${HtmlFormat.escape(exercise.sampleGrammar.asString)}</pre>""")
      case GrammarCreationXmlPart  => Html(s"""<div class="well">${exercise.grammarDescription}</div>""")
    }

    views.html.idExercises.xml.xmlExercise(user, this, ExerciseOptions("xml", 15, 30), exercise.ex, exRest, template, part, exScript)
  }

  override def playground(user: User): Html = views.html.idExercises.xml.xmlPlayground(user)

  // Result handlers

  override def onSubmitCorrectionResult(user: User, result: CompResult): Html =
    views.html.core.correction.render(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: CompResult): JsValue = result.toJson

}