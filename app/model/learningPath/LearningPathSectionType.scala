package model.learningPath

import enumeratum.{EnumEntry, PlayEnum}

sealed trait LearningPathSectionType extends EnumEntry

object LearningPathSectionType extends PlayEnum[LearningPathSectionType] {

  override val values: IndexedSeq[LearningPathSectionType] = findValues

  case object QuestionSectionType extends LearningPathSectionType {

    override val entryName = "question"

  }

  case object TextSectionType extends LearningPathSectionType {

    override val entryName = "text"

  }

}
