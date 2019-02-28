package model.tools.xml

import better.files.File
import de.uniwue.dtd.parser.DocTypeDefParser
import javax.inject._
import model.core._
import model.core.result.CompleteResultJsonProtocol
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.xml.persistence.XmlTableDefs
import model.{ExerciseState, MyYamlFormat, Points, SemanticVersionHelper, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}


@Singleton
class XmlToolMain @Inject()(val tables: XmlTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Xml", "xml") {

  private val logger = Logger(classOf[XmlToolMain])

  // Result types

  override type PartType = XmlExPart
  override type ExType = XmlExercise
  override type CollType = XmlCollection

  override type SolType = XmlSolution
  override type SampleSolType = XmlSampleSolution
  override type UserSolType = XmlUserSolution

  override type ReviewType = XmlExerciseReview

  override type ResultType = XmlEvaluationResult
  override type CompResultType = XmlCompleteResult

  override type Tables = XmlTableDefs

  // Other members

  override val hasPlayground = true

  override val toolState: ToolState = ToolState.LIVE

  override val usersCanCreateExes: Boolean = true

  override protected val exParts: Seq[XmlExPart] = XmlExParts.values

  // Yaml, Html forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[XmlCollection] = XmlExYamlProtocol.XmlCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[XmlExercise]   = XmlExYamlProtocol.XmlExYamlFormat

  override val collectionForm    : Form[XmlCollection]     = XmlExerciseForm.collectionFormat
  override val exerciseForm      : Form[XmlExercise]       = XmlExerciseForm.exerciseFormat
  override val exerciseReviewForm: Form[XmlExerciseReview] = XmlExerciseForm.exerciseReviewForm

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[XmlEvaluationResult, XmlCompleteResult] = XmlCompleteResultJsonProtocol

  // Other helper methods

  override def exerciseHasPart(exercise: XmlExercise, partType: XmlExPart): Boolean = true

  def instantiateCollection(id: Int, author: String, state: model.ExerciseState): XmlCollection =
    XmlCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): XmlExercise = XmlExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, grammarDescription = "", rootNode = "",
    samples = Seq[XmlSampleSolution](
      XmlSampleSolution(0, sample = XmlSolution(document = "", grammar = ""))
    )
  )

  override def instantiateSolution(id: Int, exercise: XmlExercise, part: XmlExPart, solution: XmlSolution, points: Points, maxPoints: Points): XmlUserSolution =
    XmlUserSolution(id, part, solution, points, maxPoints)

  // Correction

  override protected def readSolution(user: User, collection: XmlCollection, exercise: XmlExercise, part: XmlExPart)
                                     (implicit request: Request[AnyContent]): Option[XmlSolution] = request.body.asJson flatMap { jsValue =>
    jsValue match {
      case JsString(solution) =>
        part match {
          case XmlExParts.GrammarCreationXmlPart  => Some(XmlSolution(document = "", grammar = solution))
          case XmlExParts.DocumentCreationXmlPart => Some(XmlSolution(document = solution, grammar = ""))
        }
      case other              =>
        logger.error("Wrong json content: " + other.toString)
        None
    }
  }

  override protected def correctEx(user: User, solution: XmlSolution, collection: XmlCollection, exercise: XmlExercise, part: XmlExPart): Future[Try[XmlCompleteResult]] =
    Future.successful(part match {
      case XmlExParts.DocumentCreationXmlPart =>
        val solutionBaseDir = solutionDirForExercise(user.username, exercise.id).createDirectories()

        exercise.samples.headOption match {
          case None            => Failure(new Exception("There is no sample solution!"))
          case Some(xmlSample) =>
            // Write grammar
            val grammarPath: File = solutionBaseDir / s"${exercise.rootNode}.dtd"
            grammarPath.createFileIfNotExists(createParents = true).write(xmlSample.sample.grammar)

            // Write document
            val documentPath: File = solutionBaseDir / s"${exercise.rootNode}.xml"
            documentPath.createFileIfNotExists(createParents = true).write(solution.document)

            Success(XmlDocumentCompleteResult(solution.document, XmlCorrector.correctAgainstMentionedDTD(documentPath)))
        }


      case XmlExParts.GrammarCreationXmlPart =>

        val maybeSampleGrammar: Option[XmlSampleSolution] = exercise.samples
          .reduceOption((sampleG1, sampleG2) => {
            val dist1 = Java_Levenshtein.levenshteinDistance(solution.grammar, sampleG1.sample.grammar)
            val dist2 = Java_Levenshtein.levenshteinDistance(solution.grammar, sampleG2.sample.grammar)

            if (dist1 < dist2) sampleG1 else sampleG2
          })

        maybeSampleGrammar match {
          case None                => Failure[XmlCompleteResult](new Exception("Could not find a sample grammar!"))
          case Some(sampleGrammar) => Success(XmlGrammarCompleteResult(DocTypeDefParser.parseDTD(solution.grammar), sampleGrammar, exercise))
        }
    })

  // Views

  //  override def renderAdminExerciseEditForm(user: User, newEx: XmlExercise, isCreation: Boolean, toolList: ToolList)
  //                                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.xml.adminEditXmlExercise(user, XmlExerciseForm.format.fill(newEx), isCreation, this, toolList)

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[XmlExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.xml.editXmlExerciseForm(user, newExForm, isCreation, this)

  override def renderExercise(user: User, collection: XmlCollection, exercise: XmlExercise, part: XmlExPart, maybeOldSolution: Option[XmlUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val oldSolutionOrTemplate: XmlSolution = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    views.html.idExercises.xml.xmlExercise(user, collection, exercise, oldSolutionOrTemplate, part, this)
  }

  override def renderExercisePreview(user: User, collId: Int, newExercise: XmlExercise, saved: Boolean): Html =
    views.html.idExercises.xml.xmlNewExercise(user, collId, newExercise, saved, this)

  override def playground(user: User): Html = views.html.idExercises.xml.xmlPlayground(user)

}
