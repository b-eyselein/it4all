package model

import enumeratum.{EnumEntry, PlayEnum}
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq

sealed trait ExerciseState extends EnumEntry


object ExerciseState extends PlayEnum[ExerciseState] {

  override val values: IndexedSeq[ExerciseState] = findValues

  case object RESERVED extends ExerciseState

  case object CREATED extends ExerciseState

  case object ACCEPTED extends ExerciseState

  case object APPROVED extends ExerciseState

}

trait HasBaseValues {

  def id: Int

  def title: String

  def author: String

  def text: String

  def state: ExerciseState

  // FIXME: use semantic version!
  def semanticVersion: SemanticVersion

}

trait Solution[SolType] {

  val username: String

  val exerciseId: Int

  val points: Double

  val maxPoints: Double

  val solution: SolType

}

trait ExPart {

  // FIXME: use enumeratum=!=

  def urlName: String

  def partName: String

}


trait PartSolution[PartType <: ExPart, SolType] extends Solution[SolType] {

  val part: PartType

}

trait CollectionExSolution[SolType] extends Solution[SolType] {

  val collectionId: Int

}

trait ExTag {

  def render = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass = "badge badge-primary"

  def buttonContent: String

  def title: String

}

trait Exercise extends HasBaseValues

trait ExInColl extends Exercise {

  def collectionId: Int

}

trait CompleteEx[E <: Exercise] {

  def ex: E

  def preview: Html

  def tags: Seq[ExTag] = Seq.empty

}

trait SingleCompleteEx[Ex <: Exercise, PartType <: ExPart] extends CompleteEx[Ex] {

  def hasPart(partType: PartType): Boolean

}

trait PartsCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType]

trait FileCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType] {

  def templateFilename: String

  def sampleFilename: String

}

trait CompleteExInColl[Ex <: Exercise] extends CompleteEx[Ex]