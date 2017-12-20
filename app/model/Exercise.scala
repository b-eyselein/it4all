package model

import java.nio.file.Path

import controllers.exes.fileExes.FileExToolObject
import model.Enums.ExerciseState
import model.core.FileUtils
import play.api.mvc.Call
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

trait Exercise extends HasBaseValues

trait ExerciseInCollection extends Exercise {

  def collectionId: Int

}

trait CompleteEx[E <: Exercise] extends HasBaseValues {

  def ex: E

  override def baseValues: BaseValues = ex.baseValues

  def preview: Html

  def tags: Seq[ExTag] = List.empty

  def exType: String = ""

  def exerciseRoutes: Map[Call, String]

}

trait FileCompleteEx[Ex <: Exercise] extends CompleteEx[Ex] with FileUtils {

  def toolObject: FileExToolObject

  def templateFilename: String

  def sampleFilename: String

  def templateFilePath(fileEnding: String): Path = toolObject.templateDirForExercise(ex) / (templateFilename + "." + fileEnding)

  def sampleFilePath(fileEnding: String): Path = toolObject.sampleDirForExercise(ex) / (sampleFilename + "." + fileEnding)

  def available(fileEnding: String): Boolean = templateFilePath(fileEnding).toFile.exists && sampleFilePath(fileEnding).toFile.exists

}

trait ExerciseCollection[ExType <: Exercise, CompExType <: CompleteEx[ExType]] extends HasBaseValues

trait CompleteCollection extends HasBaseValues {

  type ExType <: Exercise

  type CompExType <: CompleteEx[ExType]

  type CollType <: ExerciseCollection[ExType, CompExType]

  def coll: CollType

  override def baseValues: BaseValues = coll.baseValues

  def exercisesWithFilter(filter: String): Seq[CompExType] = exercises

  def exercises: Seq[CompExType]

  def renderRest: Html

}