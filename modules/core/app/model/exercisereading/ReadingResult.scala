package model.exercisereading

import java.nio.file.Path

import com.github.fge.jsonschema.core.report.ProcessingReport
import model.JsonReadable

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.Try


case class SingleReadingResult[R <: JsonReadable](read: R, var fileResults: List[Try[Path]] = List.empty)


sealed abstract class AbstractReadingResult(/*val json: String, val jsonSchema: String*/)

case class ReadingResult[R <: JsonReadable](j: String, js: String, read: List[SingleReadingResult[R]]) extends AbstractReadingResult(j, js)

case class ReadingError(j: String, js: String, report: ProcessingReport) extends AbstractReadingResult(j, js) {
  def getCauses: Iterator[String] = report.iterator.asScala.map(_.toString)
}

case class ReadingFailure(j: String, js: String, error: Throwable) extends AbstractReadingResult(j, js)