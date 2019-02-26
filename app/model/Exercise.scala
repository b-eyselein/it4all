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

  // FIXME: use enumeratum =?=

  def urlName: String

  def partName: String

}

trait ExTag {

  def render: Html = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass: String = "badge badge-primary"

  def buttonContent: String

  def title: String

}

trait Exercise {

  def id: Int

  def semanticVersion: SemanticVersion

  def title: String

  def author: String

  def text: String

  def state: ExerciseState

  def baseValues: BaseValues


  def preview: Html

  def tags: Seq[ExTag] = Seq[ExTag]()


  // TODO: for later...

  def collectionId: Int = -1

  def collSemVer: SemanticVersion = SemanticVersionHelper.DEFAULT

}

trait ExerciseCollection extends HasBaseValues {

  val shortName: String

}
