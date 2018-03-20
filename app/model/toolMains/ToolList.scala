package model.toolMains

import scala.collection.mutable

object ToolList {

  val STEP = 5

  // FIXME: refactor!

  def FixedExToolMains: mutable.ListBuffer[FixedExToolMain] = FileExToolMains ++ ExerciseToolMains ++ ExCollectionToolMains

  def AllToolMains: mutable.ListBuffer[AToolMain] = RandomExToolMains ++ FileExToolMains ++ ExerciseToolMains ++ ExCollectionToolMains

  val RandomExToolMains: mutable.ListBuffer[RandomExerciseToolMain] = mutable.ListBuffer.empty

  val FileExToolMains: mutable.ListBuffer[FileExerciseToolMain] = mutable.ListBuffer.empty

  val ExerciseToolMains: mutable.ListBuffer[AExerciseToolMain] = mutable.ListBuffer.empty

  val ExCollectionToolMains: mutable.ListBuffer[CollectionToolMain] = mutable.ListBuffer.empty

  def addTool(tool: AToolMain): Unit = tool match {
    case w: RandomExerciseToolMain => RandomExToolMains += w
    case x: FileExerciseToolMain   => FileExToolMains += x
    case y: AExerciseToolMain      => ExerciseToolMains += y
    case z: CollectionToolMain     => ExCollectionToolMains += z
  }

  def getFileToolMainOption(urlPart: String): Option[FileExerciseToolMain] = {
    FileExToolMains.find(_.urlPart == urlPart)
  }

  def getExerciseToolMainOption(urlPart: String): Option[AExerciseToolMain] = ExerciseToolMains.find(_.urlPart == urlPart)

  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = ExCollectionToolMains.find(_.urlPart == urlPart)

  def getSingleExerciseToolMainOption(urlPart: String): Option[ASingleExerciseToolMain] = (FileExToolMains ++ ExerciseToolMains).find(_.urlPart == urlPart)

  def getFixedExToolOption(urlPart: String): Option[FixedExToolMain] = FixedExToolMains.find(_.urlPart == urlPart)

  def getRandomExToolMainOption(urlPart: String): Option[RandomExerciseToolMain] = RandomExToolMains.find(_.urlPart == urlPart)

}
