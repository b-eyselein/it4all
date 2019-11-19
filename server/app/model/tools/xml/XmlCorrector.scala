package model.tools.xml

import better.files.File
import de.uniwue.dtd.parser.DocTypeDefParser
import javax.xml.parsers.DocumentBuilderFactory
import model.core.Levenshtein
import model.core.result.SuccessType
import model.points._
import org.xml.sax.{ErrorHandler, SAXException, SAXParseException}

import scala.collection.mutable
import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Success, Try}


class CorrectionErrorHandler extends ErrorHandler {

  val errors: mutable.ListBuffer[XmlError] = mutable.ListBuffer.empty

  override def error(exception: SAXParseException): Unit = errors += XmlError.fromSAXParseException(XmlErrorType.ERROR, exception)

  override def fatalError(exception: SAXParseException): Unit = errors += XmlError.fromSAXParseException(XmlErrorType.FATAL, exception)

  override def warning(exception: SAXParseException): Unit = errors += XmlError.fromSAXParseException(XmlErrorType.WARNING, exception)

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

  def correctDocument(solution: XmlSolution, solutionBaseDir: File, exercise: XmlExercise): Try[XmlDocumentCompleteResult] =
    exercise.sampleSolutions.headOption match {
      case None            => Failure(new Exception("There is no sample solution!"))
      case Some(xmlSample) =>
        // Write grammar
        val grammarPath: File = solutionBaseDir / s"${exercise.rootNode}.dtd"
        grammarPath.createFileIfNotExists(createParents = true).write(xmlSample.sample.grammar)

        // Write document
        val documentPath: File = solutionBaseDir / s"${exercise.rootNode}.xml"
        documentPath.createFileIfNotExists(createParents = true).write(solution.document)


        val xmlErrors = XmlCorrector.correctAgainstMentionedDTD(documentPath)

        val successType = if (xmlErrors.isEmpty) SuccessType.COMPLETE else SuccessType.PARTIALLY

        Success(XmlDocumentCompleteResult(successType, xmlErrors))
    }

  // Grammar correction

  private def findNearestGrammarSample(learnerSolution: String, sampleSolutions: Seq[XmlSampleSolution]): Option[XmlSampleSolution] =
    sampleSolutions.reduceOption((sampleG1, sampleG2) => {
      val dist1 = Levenshtein.levenshtein(learnerSolution, sampleG1.sample.grammar)
      val dist2 = Levenshtein.levenshtein(learnerSolution, sampleG2.sample.grammar)

      if (dist1 < dist2) sampleG1 else sampleG2
    })

  def correctGrammar(solution: XmlSolution, exercise: XmlExercise): Try[XmlCompleteResult] = findNearestGrammarSample(solution.grammar, exercise.sampleSolutions) match {
    case None                                    => Failure[XmlCompleteResult](new Exception("Could not find a sample grammar!"))
    case Some(sampleSolution: XmlSampleSolution) =>

      DocTypeDefParser.tryParseDTD(sampleSolution.sample.grammar) map { sampleGrammar =>

        val dtdParseResult = DocTypeDefParser.parseDTD(solution.grammar)

        val allMatches = DocTypeDefMatcher.doMatch(dtdParseResult.dtd.asElementLines, sampleGrammar.asElementLines).allMatches

        val points = addUp(allMatches.map(_.points))

        val maxPoints = addUp(allMatches.map(_.maxPoints))

        val successType: SuccessType = points.quarters.toDouble / maxPoints.quarters match {
          case it if 0 <= it && it <= 0.5 => SuccessType.NONE
          case it if 0.5 < it && it < 1   => SuccessType.PARTIALLY
          case 1                          => SuccessType.COMPLETE
          case _                          => SuccessType.ERROR
        }

        XmlGrammarCompleteResult(successType, dtdParseResult.parseErrors, allMatches, points, maxPoints)
      }
  }


}


