package model.learningPath

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq


// Types of questions

sealed trait LearningPathQuestionTypes extends EnumEntry

object LearningPathQuestionTypes extends Enum[LearningPathQuestionTypes] {

  override val values: IndexedSeq[LearningPathQuestionTypes] = findValues

  case object BooleanQuestionType extends LearningPathQuestionTypes {

    override val entryName = "boolean"

  }

}
