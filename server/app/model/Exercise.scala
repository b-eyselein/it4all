package model

import enumeratum.{EnumEntry, PlayEnum}

final case class LongString(str: String) {
  // TODO: use and read / write from and to file!
}

trait ExPart extends EnumEntry {
  // FIXME: use enumeratum =?=

  def urlName: String

  def partName: String

}

trait ExParts[EP <: ExPart] extends PlayEnum[EP]


trait ExTag {

  val buttonContent: String

  val title: String

}

trait Exercise {

  val id: Int

  val collectionId: Int

  val semanticVersion: SemanticVersion

  val title: String

  val author: String

  val text: LongText

  val state: ExerciseState


  protected type SolutionType

  protected type SampleSolutionType <: SampleSolution[SolutionType]

  val sampleSolutions: Seq[SampleSolutionType]


  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait FileExercise[PartType <: ExPart] extends Exercise {

  override protected type SolutionType = Seq[ExerciseFile]

  override protected type SampleSolutionType = FilesSampleSolution


  def filesForExercisePart(part: PartType): LoadExerciseFilesMessage

}

final case class ExerciseCollection(
  id: Int,
  toolId: String,
  title: String,
  author: String,
  text: String,
  state: ExerciseState,
  shortName: String
)
