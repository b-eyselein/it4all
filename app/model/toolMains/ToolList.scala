package model.toolMains

import javax.inject.{Inject, Singleton}

import scala.collection.JavaConverters._

@Singleton
class ToolList @Inject()(javaToolMains: java.util.Set[AToolMain]) {

  val toolMains: Seq[AToolMain] = javaToolMains.asScala.toSeq

  def getFileToolMainOption(urlPart: String): Option[FileExerciseToolMain] = toolMains.collectFirst {
    case fetm: FileExerciseToolMain if fetm.urlPart == urlPart => fetm
  }

  def getExerciseToolMainOption(urlPart: String): Option[IdExerciseToolMain] = toolMains.collectFirst {
    case ietm: IdExerciseToolMain if ietm.urlPart == urlPart => ietm
  }

  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = toolMains.collectFirst {
    case ctm: CollectionToolMain if ctm.urlPart == urlPart => ctm
  }

  def getSingleExerciseToolMainOption(urlPart: String): Option[ASingleExerciseToolMain] = toolMains.collectFirst {
    case setm: ASingleExerciseToolMain if setm.urlPart == urlPart => setm
  }

  def getFixedExToolOption(urlPart: String): Option[FixedExToolMain] = toolMains.collectFirst {
    case fetm: FixedExToolMain if fetm.urlPart == urlPart => fetm
  }

  def getRandomExToolMainOption(urlPart: String): Option[RandomExerciseToolMain] = toolMains.collectFirst {
    case retm: RandomExerciseToolMain if retm.urlPart == urlPart => retm
  }

}
