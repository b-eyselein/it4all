package model.exercisereading;

import java.util.List;

import model.WithId;
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.core.exceptions.ProcessingException

sealed abstract class AbstractReadingResult(val json: String, val jsonSchema: String)

case class ReadingResult[T <: WithId](j: String, js: String, read: List[T]) extends AbstractReadingResult(j, js)

case class ReadingError(j: String, js: String, report: ProcessingReport) extends AbstractReadingResult(j, js)

case class ReadingFailure(j: String, js: String, error: ProcessingException) extends AbstractReadingResult(j, js)