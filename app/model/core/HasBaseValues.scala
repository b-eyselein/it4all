package model.core

import com.google.common.base.Splitter
import model.Enums.ExerciseState
import play.twirl.api.Html

object HasBaseValues {

  val SPLIT_CHAR = "#"

  val SPLITTER: Splitter = Splitter.fixedLength(100).omitEmptyStrings()

  val NEW_LINE_SPLITTER: Splitter = Splitter.on("\n")

}

abstract class HasBaseValues(val id: Int, val title: String, val author: String, val text: String, val state: ExerciseState) {

  def renderRest = new Html("")

  def tags: List[ExTag] = List.empty

}