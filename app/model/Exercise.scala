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

trait CompleteEx[E <: Exercise] extends HasBaseValues {

  def ex: E

  def preview: Html

  def tags: Seq[ExTag] = Seq.empty


  override def id: Int = ex.id

  override def title: String = ex.title

  override def author: String = ex.author

  override def text: String = ex.text

  override def state: ExerciseState = ex.state

}

trait SingleCompleteEx[Ex <: Exercise, PartType <: ExPart] extends CompleteEx[Ex] {

  def hasPart(partType: PartType): Boolean

}

//trait ResultForPart[PartType <: ExPart] {
//
//  val username: String
//  val exerciseId: Int
//  val part: PartType
//  val points: Double
//  val maxPoints: Double
//
//}

trait PartsCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType] {

  def textForPart(urlName: String): String = ex.text

}

trait FileCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType] {

  def templateFilename: String

  def sampleFilename: String

}

trait CompleteExInColl[Ex <: Exercise] extends CompleteEx[Ex]