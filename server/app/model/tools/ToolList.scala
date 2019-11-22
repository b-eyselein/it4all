package model.tools

import model.tools.collectionTools.CollectionToolMain
import model.tools.collectionTools.programming.ProgToolMain
import model.tools.collectionTools.regex.RegexToolMain
import model.tools.collectionTools.rose.RoseToolMain
import model.tools.collectionTools.sql.SqlToolMain
import model.tools.collectionTools.uml.UmlToolMain
import model.tools.collectionTools.web.WebToolMain
import model.tools.collectionTools.xml.XmlToolMain
import model.tools.randomTools.{BoolToolMain, NaryToolMain, RandomExerciseToolMain}

object ToolList {

  val toolMains: Seq[AToolMain] = Seq(
    BoolToolMain, NaryToolMain,
    ProgToolMain, RegexToolMain, RoseToolMain, SqlToolMain, UmlToolMain, WebToolMain, XmlToolMain
  )


  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = toolMains.collectFirst {
    case ctm: CollectionToolMain if ctm.urlPart == urlPart => ctm
  }

  def getRandomExToolMainOption(urlPart: String): Option[RandomExerciseToolMain] = toolMains.collectFirst {
    case retm: RandomExerciseToolMain if retm.urlPart == urlPart => retm
  }

}
