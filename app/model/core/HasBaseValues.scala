package model.core

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Splitter
import model.Enums.ExerciseState
import play.twirl.api.Html

object HasBaseValues {

  val SPLIT_CHAR = "#"

  val SPLITTER: Splitter = Splitter.fixedLength(100).omitEmptyStrings()

  val NEW_LINE_SPLITTER: Splitter = Splitter.on("\n")

}

abstract class HasBaseValues(var id: Int,
                             @JsonProperty(required = true) var title: String,
                             @JsonProperty(required = true) var author: String,
                             @JsonProperty(required = true) var text: String,
                             var state: ExerciseState) {

  def renderRest = new Html("")

}