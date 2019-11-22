package model.tools

import better.files._
import model.core.CoreConsts._
import model.learningPath.{LearningPath, LearningPathYamlProtocol}
import net.jcazevedo.moultingyaml._
import play.api.Logger

abstract class AToolMain(consts: ToolConsts) {

  private val logger = Logger(classOf[AToolMain])

  // Other members

  final val toolName : String    = consts.toolName
  final val urlPart  : String    = consts.toolId
  final val toolState: ToolState = consts.toolState

  val hasTags      : Boolean = false
  val hasPlayground: Boolean = false

  private val learningPathsYamlFormat: YamlFormat[LearningPath] = LearningPathYamlProtocol.learningPathYamlFormat

  // Folders

  protected val exerciseResourcesFolder: File = File.currentWorkingDirectory / "conf" / "resources" / urlPart

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / urlPart / solutionsSubDir / username / String.valueOf(collId) / String.valueOf(exId)

  // DB

  //  def futureLearningPaths: Future[Seq[LearningPath]] = tables.futureLearningPaths(urlPart)

  //  def futureLearningPathById(id: Int): Future[Option[LearningPath]] = tables.futureLearningPathById(urlPart, id)

  //  def futureSaveLearningPaths(readLearningPaths: Seq[LearningPath]): Future[Boolean] = tables.futureSaveLearningPaths(readLearningPaths)

  // Helper methods

  def readLearningPaths: Seq[LearningPath] = {
    val learningPathFile: File   = exerciseResourcesFolder / "learningPath.yaml"
    val content         : String = learningPathFile.contentAsString

    learningPathsYamlFormat.read(content.parseYaml)

    ???
  }


}
