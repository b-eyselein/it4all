package model

import java.nio.file.Path

import model.Enums.ExerciseState
import model.core.{ExPart, FileUtils, MyWrapper, Wrappable}
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

trait CompleteEx[E <: Exercise] extends Wrappable with HasBaseValues {

  def ex: E

  def preview: Html

  def tags: Seq[ExTag] = Seq.empty

  def exType: String = ""

  override def wrapped: CompleteExWrapper

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

  def templateFilePath(toolMain: AToolMain, fileEnding: String): Path = toolMain.templateDirForExercise(ex.id) / (templateFilename + "." + fileEnding)

  def sampleFilePath(toolMain: AToolMain, fileEnding: String): Path = toolMain.sampleDirForExercise(ex.id) / (sampleFilename + "." + fileEnding)

  def available(toolMain: AToolMain, fileEnding: String): Boolean = templateFilePath(toolMain, fileEnding).toFile.exists && sampleFilePath(toolMain, fileEnding).toFile.exists

}

trait CompleteExInColl[Ex <: Exercise] extends CompleteEx[Ex] {

}

abstract class CompleteExWrapper extends MyWrapper {

  type Ex <: Exercise

  type CompEx <: CompleteEx[Ex]

  val compEx: CompEx

  override def wrappedObj: Wrappable = compEx

}