package model

import enumeratum.{EnumEntry, PlayEnum}
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq

sealed abstract class ExerciseState(val german: String) extends EnumEntry


object ExerciseState extends PlayEnum[ExerciseState] {

  override val values: IndexedSeq[ExerciseState] = findValues

  case object RESERVED extends ExerciseState("Reserviert")

  case object CREATED extends ExerciseState("Erstellt")

  case object ACCEPTED extends ExerciseState("Akzeptiert")

  case object APPROVED extends ExerciseState("Zugelassen")

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

trait HasBaseValuesInColl extends HasBaseValues {

  def collectionId: Int

  def collSemVer: SemanticVersion

}

trait CompleteEx {

  type E <: HasBaseValues

  def ex: E


  def id: Int = ex.id

  def semanticVersion: SemanticVersion = ex.semanticVersion

  def title: String = ex.title

  def author: String = ex.author

  def text: String = ex.text

  def state: ExerciseState = ex.state

  def baseValues: BaseValues = ex.baseValues


  def preview: Html

  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait SingleCompleteEx[PartType <: ExPart] extends CompleteEx {

  def hasPart(partType: PartType): Boolean

}

trait CompleteExInColl extends CompleteEx {

  override type E <: HasBaseValuesInColl

  def collectionId: Int = ex.collectionId

  def collSemVer: SemanticVersion = ex.collSemVer

}