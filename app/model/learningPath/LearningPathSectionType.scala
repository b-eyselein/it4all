package model.learningPath

import enumeratum._

import scala.collection.immutable.IndexedSeq


sealed trait LearningPathSectionType extends EnumEntry


object LearningPathSectionType extends Enum[LearningPathSectionType] {

  override val values: IndexedSeq[LearningPathSectionType] = findValues

  case object QuestionSectionType extends LearningPathSectionType {

    override val entryName = "question"

  }

  case object TextSectionType extends LearningPathSectionType {

    override val entryName = "text"

  }

}
