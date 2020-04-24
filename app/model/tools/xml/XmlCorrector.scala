package model.tools.xml

import better.files.File
import de.uniwue.dtd.parser.DocTypeDefParser
import javax.xml.parsers.DocumentBuilderFactory
import model.core.Levenshtein
import model.core.result.SuccessType
import model.points._
import model.tools.SampleSolution
import model.tools.xml.XmlTool.ElementLineComparison
import org.xml.sax.{ErrorHandler, SAXException, SAXParseException}
import play.api.Logger

import scala.collection.mutable

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

  private val logger = Logger(XmlCorrector.getClass)

  private def onError(
    msg: String,
    solutionSaved: Boolean,
    maybeException: Option[Throwable] = None,
    maxPoints: Points = (-1).points
  ): XmlAbstractResult = {
    maybeException match {
      case None            => logger.error(msg)
      case Some(exception) => logger.error(msg, exception)
    }

    XmlInternalErrorResult(solutionSaved, maxPoints)
  }

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
    exerciseContent: XmlExerciseContent,
    solutionSaved: Boolean
  ): XmlAbstractResult = exerciseContent.sampleSolutions.headOption match {
    case None            => onError("There is no sample solution!", solutionSaved)
    case Some(xmlSample) =>
      // Write grammar
      (solutionBaseDir / s"${exerciseContent.rootNode}.dtd")
        .createFileIfNotExists(createParents = true)
        .write(xmlSample.sample.grammar)

      // Write document
      val documentPath: File = solutionBaseDir / s"${exerciseContent.rootNode}.xml"
      documentPath.createFileIfNotExists(createParents = true).write(solution.document)

      val xmlErrors = XmlCorrector.correctAgainstMentionedDTD(documentPath)

      val successType = if (xmlErrors.isEmpty) SuccessType.COMPLETE else SuccessType.PARTIALLY

      val points    = (-1).points
      val maxPoints = (-1).points

      XmlCompleteResult(
        successType,
        documentResult = xmlErrors,
        grammarResult = None,
        points,
        maxPoints,
        solutionSaved
      )
  }

  // Grammar correction

  private def findNearestGrammarSample(
    learnerSolution: String,
    sampleSolutions: Seq[SampleSolution[XmlSolution]]
  ): Option[SampleSolution[XmlSolution]] =
    sampleSolutions.reduceOption((sampleG1, sampleG2) => {
      val dist1 = Levenshtein.levenshtein(learnerSolution, sampleG1.sample.grammar)
      val dist2 = Levenshtein.levenshtein(learnerSolution, sampleG2.sample.grammar)

      if (dist1 < dist2) sampleG1 else sampleG2
    })

  def correctGrammar(
    solution: XmlSolution,
    sampleSolutions: Seq[SampleSolution[XmlSolution]],
    solutionSaved: Boolean
  ): XmlAbstractResult = findNearestGrammarSample(solution.grammar, sampleSolutions) match {
    case None => onError("Could not find a sample grammar!", solutionSaved)
    case Some(sampleSolution) =>
      DocTypeDefParser
        .tryParseDTD(sampleSolution.sample.grammar)
        .fold(
          error => onError("Error while parseing dtd", solutionSaved, Some(error)),
          sampleGrammar => {
            val dtdParseResult = DocTypeDefParser.parseDTD(solution.grammar)

            val matchingResult: ElementLineComparison =
              DocTypeDefMatcher.doMatch(dtdParseResult.dtd.asElementLines, sampleGrammar.asElementLines)

            val points = addUp(matchingResult.allMatches.map(_.points))

            val maxPoints = addUp(matchingResult.allMatches.map(_.maxPoints))

            val successType: SuccessType = points.quarters.toDouble / maxPoints.quarters match {
              case it if 0 <= it && it <= 0.5 => SuccessType.NONE
              case it if 0.5 < it && it < 1   => SuccessType.PARTIALLY
              case 1                          => SuccessType.COMPLETE
              case _                          => SuccessType.ERROR
            }

            XmlCompleteResult(
              successType,
              documentResult = Seq.empty,
              grammarResult = Some(XmlGrammarResult(dtdParseResult.parseErrors, matchingResult)),
              points,
              maxPoints,
              solutionSaved
            )
          }
        )
  }

}
