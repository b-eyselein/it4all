package model.exercisereading

import java.nio.file.Path

import com.github.fge.jsonschema.core.report.ProcessingReport
import model.JsonReadable

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.util.Try


case class SingleReadingResult[R <: JsonReadable](read: R, var fileResults: List[Try[Path]] = List.empty)


sealed abstract class AbstractReadingResult

case class ReadingResult[R <: JsonReadable](read: List[SingleReadingResult[R]]) extends AbstractReadingResult

case class ReadingError(json: String, jsonSchema: String, report: ProcessingReport) extends AbstractReadingResult {
  def getCauses: Iterator[String] = report.iterator.asScala.map(_.toString)
}

case class ReadingFailure(error: Throwable) extends AbstractReadingResult