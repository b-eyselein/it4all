package model

import java.nio.file.Path

import controllers.fileExes.{FileExToolObject, FileType}
import model.Enums.ExerciseState
import model.core.FileUtils
import play.api.mvc.Call
import play.twirl.api.Html

case class BaseValues(id: Int, title: String, author: String, text: String, state: ExerciseState)

trait HasBaseValues {

  val baseValues: BaseValues

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

trait CompleteEx[B <: HasBaseValues] {

  def ex: HasBaseValues

  def preview: Html

  def tags: List[ExTag] = List.empty

  def renderListRest: Html

  def exerciseRoutes: List[(Call, String)]

}

trait FileCompleteEx[B <: Exercise] extends CompleteEx[B] with FileUtils {

  def toolObject: FileExToolObject

  def templateFilename: String

  def sampleFilename: String

  def templateFilePath(fileEnding: String): Path = toolObject.sampleDir / (templateFilename + "." + fileEnding)

  def sampleFilePath(fileEnding: String): Path = toolObject.sampleDir / (sampleFilename + "." + fileEnding)

  def available(fileType: FileType): Boolean = templateFilePath(fileType.fileEnding).toFile.exists &&
    sampleFilePath(fileType.fileEnding).toFile.exists

}