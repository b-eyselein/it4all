package model.exercisereading;

import java.util.List;

import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import model.JsonReadable

sealed abstract class AbstractReadingResult(val json: String, val jsonSchema: String)

case class ReadingResult[R <: JsonReadable](j: String, js: String, read: List[R]) extends AbstractReadingResult(j, js)

case class ReadingError(j: String, js: String, report: ProcessingReport) extends AbstractReadingResult(j, js)

case class ReadingFailure(j: String, js: String, error: Throwable) extends AbstractReadingResult(j, js)