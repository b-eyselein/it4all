package model.core


import java.nio.file.Path

import com.github.fge.jsonschema.core.report.ProcessingReport
import model.HasBaseValues

import scala.collection.JavaConverters._
import scala.util.Try


sealed abstract class AbstractReadingResult

// FIXME: delete if not used anymore...!

case class ReadingError(json: String, jsonSchema: String, report: ProcessingReport) extends AbstractReadingResult {
  def getCauses: Iterator[String] = report.iterator.asScala.map(_.toString)
}

case class ReadingFailure(error: Throwable) extends AbstractReadingResult

case class ReadingResult[R <: HasBaseValues](read: List[SingleReadingResult[R]]) extends AbstractReadingResult


case class SingleReadingResult[R <: HasBaseValues](read: R, var fileResults: List[Try[Path]] = List.empty)
