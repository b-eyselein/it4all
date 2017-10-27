package model.sql

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Splitter

import scala.collection.JavaConverters._

object SqlSample {
  val NEWLINE_SPLITTER: Splitter = Splitter.on("\n")
}

case class SqlSample(id: Int, exerciseId: Int, @JsonProperty(required = true) sample: String) {

  def getSample: List[String] = SqlSample.NEWLINE_SPLITTER.splitToList(sample).asScala.toList

}
