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

final case class BaseValues(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState)

trait HasBaseValues {

  def id: Int

  def title: String

  def author: String

  def text: String

  def state: ExerciseState

  // FIXME: use semantic version!
  def semanticVersion: SemanticVersion

  def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

}

trait ExPart {

  // FIXME: use enumeratum=!=

  def urlName: String

  def partName: String

}

trait ExTag {

  def render: Html = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass: String = "badge badge-primary"

  def buttonContent: String

  def title: String

}

trait Exercise extends HasBaseValues

trait ExInColl extends Exercise {

  def collectionId: Int

  def collSemVer: SemanticVersion

}

trait CompleteEx[E <: Exercise] {

  def ex: E

  def preview: Html

  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait SingleCompleteEx[Ex <: Exercise, PartType <: ExPart] extends CompleteEx[Ex] {

  def hasPart(partType: PartType): Boolean

}

trait FileCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType] {

  def templateFilename: String

  def sampleFilename: String

}

trait CompleteExInColl[Ex <: Exercise] extends CompleteEx[Ex]