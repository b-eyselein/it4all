package model

import java.nio.file.Path

import controllers.exes.AToolMain
import model.Enums.ExerciseState
import model.core.{ExPart, FileUtils}
import play.twirl.api.Html

case class BaseValues(id: Int, title: String, author: String, text: String, state: ExerciseState)

trait HasBaseValues {

  def baseValues: BaseValues

  def id: Int = baseValues.id

  def title: String = baseValues.title

  def author: String = baseValues.author

  def text: String = baseValues.text

  def state: ExerciseState = baseValues.state

}

trait ExTag {

  def render = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass = "label label-primary"

  def buttonContent: String = toString

  def title: String = toString

}

trait ExerciseIdentifier {

  val id: Int

}

trait Exercise extends HasBaseValues

trait ExerciseInCollection extends Exercise {

  def collectionId: Int

}

trait CompleteEx[E <: Exercise] extends HasBaseValues {

  def ex: E

  override def baseValues: BaseValues = ex.baseValues

  def preview: Html

  def tags: Seq[ExTag] = Seq.empty

  def exType: String = ""

}

trait PartsCompleteEx[E <: Exercise, PartType <: ExPart] extends CompleteEx[E] {

  def hasPart(partType: PartType): Boolean

  def textForPart(urlName: String): String = text

}

trait FileCompleteEx[Ex <: Exercise] extends CompleteEx[Ex] with FileUtils {

  def templateFilename: String

  def sampleFilename: String

  def templateFilePath(toolMain: AToolMain[_, _], fileEnding: String): Path = toolMain.templateDirForExercise(ex.id) / (templateFilename + "." + fileEnding)

  def sampleFilePath(toolMain: AToolMain[_, _], fileEnding: String): Path = toolMain.sampleDirForExercise(ex.id) / (sampleFilename + "." + fileEnding)

  def available(toolMain: AToolMain[_, _], fileEnding: String): Boolean = templateFilePath(toolMain, fileEnding).toFile.exists && sampleFilePath(toolMain, fileEnding).toFile.exists

}

trait ExerciseCollection[ExType <: Exercise, CompExType <: CompleteEx[ExType]] extends HasBaseValues


trait CompleteCollection[Ex <: Exercise, CompEx <: CompleteEx[Ex], Coll <: ExerciseCollection[Ex, CompEx]] extends HasBaseValues {

  def coll: Coll

  override def baseValues: BaseValues = coll.baseValues

  def exercisesWithFilter(filter: String): Seq[CompEx] = exercises

  def exercises: Seq[CompEx]

  def renderRest: Html

}

abstract class CompleteCollectionWrapper extends HasBaseValues {

  type Ex <: Exercise

  type CompEx <: CompleteEx[Ex]

  type Coll <: ExerciseCollection[Ex, CompEx]

  type CompColl <: CompleteCollection[Ex, CompEx, Coll]

  val coll: CompColl

  override def baseValues: BaseValues = coll.baseValues

}
