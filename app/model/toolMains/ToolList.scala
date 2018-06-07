package model.toolMains

import scala.collection.mutable.ListBuffer

object ToolList {

  val STEP = 10

  // FIXME: refactor!

  def FixedExToolMains: ListBuffer[FixedExToolMain] = FileExToolMains ++ ExerciseToolMains ++ ExCollectionToolMains

  def AllToolMains: ListBuffer[AToolMain] = RandomExToolMains ++ FileExToolMains ++ ExerciseToolMains ++ ExCollectionToolMains

  val RandomExToolMains: ListBuffer[RandomExerciseToolMain] = ListBuffer.empty

  val FileExToolMains: ListBuffer[FileExerciseToolMain] = ListBuffer.empty

  val ExerciseToolMains: ListBuffer[IdExerciseToolMain] = ListBuffer.empty

  val ExCollectionToolMains: ListBuffer[CollectionToolMain] = ListBuffer.empty

  def addTool(tool: AToolMain): Unit = tool match {
    case w: RandomExerciseToolMain => RandomExToolMains += w
    case x: FileExerciseToolMain   => FileExToolMains += x
    case y: IdExerciseToolMain     => ExerciseToolMains += y
    case z: CollectionToolMain     => ExCollectionToolMains += z
  }

  def getFileToolMainOption(urlPart: String): Option[FileExerciseToolMain] = {
    FileExToolMains.find(_.urlPart == urlPart)
  }

  def getExerciseToolMainOption(urlPart: String): Option[IdExerciseToolMain] = ExerciseToolMains.find(_.urlPart == urlPart)

  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = ExCollectionToolMains.find(_.urlPart == urlPart)

  def getSingleExerciseToolMainOption(urlPart: String): Option[ASingleExerciseToolMain] = (FileExToolMains ++ ExerciseToolMains).find(_.urlPart == urlPart)

  def getFixedExToolOption(urlPart: String): Option[FixedExToolMain] = FixedExToolMains.find(_.urlPart == urlPart)

  def getRandomExToolMainOption(urlPart: String): Option[RandomExerciseToolMain] = RandomExToolMains.find(_.urlPart == urlPart)

}
