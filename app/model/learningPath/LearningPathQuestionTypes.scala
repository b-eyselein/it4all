package model.learningPath

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq


// Types of questions

sealed trait LearningPathQuestionTypes extends EnumEntry

object LearningPathQuestionTypes extends PlayEnum[LearningPathQuestionTypes] {

  override val values: IndexedSeq[LearningPathQuestionTypes] = findValues

  case object BooleanQuestionType extends LearningPathQuestionTypes {

    override val entryName = "boolean"

  }

}
