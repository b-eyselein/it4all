package model.toolMains

import javax.inject.{Inject, Singleton}

import scala.collection.JavaConverters._

@Singleton
class ToolList @Inject()(javaToolMains: java.util.Set[AToolMain]) {

  val toolMains: Seq[AToolMain] = javaToolMains.asScala.toSeq


  def collectionToolMains: Seq[CollectionToolMain] = toolMains.collect {
    case x: CollectionToolMain => x
  }

  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = toolMains.collectFirst {
    case ctm: CollectionToolMain if ctm.urlPart == urlPart => ctm
  }


  def randomExToolMains: Seq[RandomExerciseToolMain] = toolMains.collect {
    case x: RandomExerciseToolMain => x
  }

  def getRandomExToolMainOption(urlPart: String): Option[RandomExerciseToolMain] = toolMains.collectFirst {
    case retm: RandomExerciseToolMain if retm.urlPart == urlPart => retm
  }

}
