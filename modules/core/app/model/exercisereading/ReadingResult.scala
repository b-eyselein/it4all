package model.exercisereading

import scala.collection.JavaConverters.{asScalaIteratorConverter, seqAsJavaListConverter}

import com.github.fge.jsonschema.core.report.ProcessingReport

import model.JsonReadable

sealed abstract class AbstractReadingResult(val json: String, val jsonSchema: String)

case class ReadingResult[R <: JsonReadable](j: String, js: String, read: List[R]) extends AbstractReadingResult(j, js) {

  def javaRead: java.util.List[R] = read.asJava

}

case class ReadingError(j: String, js: String, report: ProcessingReport) extends AbstractReadingResult(j, js) {
  def getCauses: Iterator[String] = report.iterator.asScala.map(_.toString)
}

case class ReadingFailure(j: String, js: String, error: Throwable) extends AbstractReadingResult(j, js)