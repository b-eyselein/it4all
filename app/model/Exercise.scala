package model

import java.nio.file.Path

import model.Enums.ExerciseState
import model.core.{ExPart, FileUtils}
import model.toolMains.AToolMain
import play.twirl.api.Html

trait HasBaseValues {

  def id: Int

  def title: String

  def author: String

  def text: String

  def state: ExerciseState

}

trait Solution {

  val username: String

  val exerciseId: Int

}

trait PartSolution extends Solution {

  type PartType <: ExPart

  val part: PartType

}

trait CollectionExSolution extends Solution {

  val collectionId: Int

}

trait ExTag {

  def render = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass = "label label-primary"

  def buttonContent: String = toString

  def title: String = toString

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

trait PartsCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType] {

  def textForPart(urlName: String): String = ex.text

}

trait FileCompleteEx[Ex <: Exercise, PartType <: ExPart] extends SingleCompleteEx[Ex, PartType] with FileUtils {

  def templateFilename: String

  def sampleFilename: String

}

trait CompleteExInColl[Ex <: Exercise] extends CompleteEx[Ex]