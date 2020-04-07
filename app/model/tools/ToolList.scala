package model.tools

import model.tools.programming.ProgToolMain
import model.tools.regex.RegexToolMain
import model.tools.sql.SqlToolMain
import model.tools.uml.UmlToolMain
import model.tools.web.WebToolMain
import model.tools.xml.XmlToolMain

object ToolList {

  val collectionToolMains: List[CollectionToolMain] = List(
    ProgToolMain,
    RegexToolMain,
    //    RoseToolMain,
    SqlToolMain,
    UmlToolMain,
    WebToolMain,
    XmlToolMain
  )

  def getExCollToolMainOption(urlPart: String): Option[CollectionToolMain] = collectionToolMains.collectFirst {
    case ctm if ctm.id == urlPart => ctm
  }

}
