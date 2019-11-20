package model.tools

import javax.inject.{Inject, Singleton}
import model.tools.collectionTools.CollectionToolMain
import model.tools.randomTools.RandomExerciseToolMain

import scala.jdk.CollectionConverters._

@Singleton
class ToolList @Inject()(javaToolMains: java.util.Set[AToolMain]) {

  val toolMains: Seq[AToolMain] = javaToolMains.asScala.toSeq


  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = toolMains.collectFirst {
    case ctm: CollectionToolMain if ctm.urlPart == urlPart => ctm
  }

  def getRandomExToolMainOption(urlPart: String): Option[RandomExerciseToolMain] = toolMains.collectFirst {
    case retm: RandomExerciseToolMain if retm.urlPart == urlPart => retm
  }

}
