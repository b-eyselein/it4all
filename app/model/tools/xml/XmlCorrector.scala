package model.tools.xml

import better.files.File
import de.uniwue.dtd.parser.DocTypeDefParser
import model.core.Levenshtein
import model.points._
import model.tools.xml.XmlTool.ElementLineComparison
import org.xml.sax.{ErrorHandler, SAXException, SAXParseException}

import javax.xml.parsers.DocumentBuilderFactory
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class CorrectionErrorHandler extends ErrorHandler {

  val errors: mutable.ListBuffer[XmlError] = mutable.ListBuffer.empty

  override def error(exception: SAXParseException): Unit =
    errors += XmlError.fromSAXParseException(XmlErrorType.ERROR, exception)

  override def fatalError(exception: SAXParseException): Unit =
    errors += XmlError.fromSAXParseException(XmlErrorType.FATAL, exception)

  override def warning(exception: SAXParseException): Unit =
    errors += XmlError.fromSAXParseException(XmlErrorType.WARNING, exception)

}

object XmlCorrector {

  // Document correction

  private val DocBuilderFactory = DocumentBuilderFactory.newInstance
  DocBuilderFactory.setValidating(true)

  def correctAgainstMentionedDTD(xml: File): Seq[XmlError] = {
    val errorHandler = new CorrectionErrorHandler

    try {
      val builder = DocBuilderFactory.newDocumentBuilder
      builder.setErrorHandler(errorHandler)
      builder.parse(xml.toJava)
      ()
    } catch {
      case _: SAXException => // Ignore...
    }

    errorHandler.errors.toSeq
  }

  def correctDocument(
    solution: XmlSolution,
    solutionBaseDir: File,
    exerciseContent: XmlExerciseContent
  ): Try[XmlResult] = exerciseContent.sampleSolutions.headOption match {
    case None            => Failure(new Exception("There is no sample solution!"))
    case Some(xmlSample) =>
      // Write grammar
      val grammarPath = solutionBaseDir / s"${exerciseContent.rootNode}.dtd"
      grammarPath.createFileIfNotExists(createParents = true).write(xmlSample.grammar)

      // Write document
      val documentPath: File = solutionBaseDir / s"${exerciseContent.rootNode}.xml"
      documentPath.createFileIfNotExists(createParents = true).write(solution.document)

      val xmlErrors = XmlCorrector.correctAgainstMentionedDTD(documentPath)

      Success(
        XmlResult(
          // FIXME: points!
          points = (-1).points,
          maxPoints = (-1).points,
          documentResult = Some(XmlDocumentResult(xmlErrors))
        )
      )
  }

  // Grammar correction

  private def findNearestGrammarSample(
    learnerSolution: String,
    sampleSolutions: Seq[XmlSolution]
  ): Option[XmlSolution] = sampleSolutions.reduceOption((sampleG1, sampleG2) => {
    val dist1 = Levenshtein.levenshtein(learnerSolution, sampleG1.grammar)
    val dist2 = Levenshtein.levenshtein(learnerSolution, sampleG2.grammar)

    if (dist1 < dist2) sampleG1 else sampleG2
  })

  // TODO: check all grammars, use best?
  def correctGrammar(solution: XmlSolution, sampleSolutions: Seq[XmlSolution]): Try[XmlResult] = for {
    sampleSolution <- findNearestGrammarSample(solution.grammar, sampleSolutions)
      .map(Success(_))
      .getOrElse(Failure(new Exception("Could not find a sample grammar!")))

    sampleGrammar <- DocTypeDefParser.tryParseDTD(sampleSolution.grammar)
  } yield {
    val dtdParseResult = DocTypeDefParser.parseDTD(solution.grammar)

    val matchingResult: ElementLineComparison = DocTypeDefMatcher.doMatch(dtdParseResult.dtd.asElementLines, sampleGrammar.asElementLines)

    XmlResult(
      points = addUp(matchingResult.allMatches.map(_.points)),
      maxPoints = addUp(matchingResult.allMatches.map(_.maxPoints)),
      grammarResult = Some(XmlGrammarResult(dtdParseResult.parseErrors, matchingResult))
    )
  }

}
