package model.tools

import play.mvc.Call

import ToolState._
import play.twirl.api.Html
import scala.collection.mutable.MutableList
import controllers.core.ToolObject

case class Tool(name: String, state: ToolState, index: Call, decoration: String = null)

case class ToolGroup(name: String, tools: List[Tool])

object ToolList {

//  val tools = List(
//    Tool("Programmierung", LIVE, controllers.programming.routes.ProgController.index()),
//    Tool("Sql", LIVE, controllers.sql.routes.SqlController.index()),
//    Tool("Tabellenkalkulation", LIVE, controllers.spread.routes.SpreadController.index()),
//    Tool("Web", LIVE, controllers.web.routes.WebController.index(), "<!DOCTYPE html>\n<html>...</html>"),
//    Tool("Xml", LIVE, controllers.xml.routes.XmlController.index()),
//
//    Tool("Uml", BETA, controllers.uml.routes.UmlController.index()),
//    Tool("Auswahlfragen", BETA, controllers.questions.routes.QuestionController.index()))
//
//  val toolGroups = List(
//    ToolGroup(
//      "Allgemeiner Teil",
//      List(
//        Tool("Boolesche Algebra", LIVE, controllers.bool.routes.BoolController.index()),
//        Tool("MindMap", ALPHA, controllers.mindmap.routes.MindmapController.index()),
//        Tool("Ebnf", ALPHA, controllers.ebnf.routes.EBNFController.index()),
//        Tool("Zahlensysteme", LIVE, controllers.routes.NaryController.index()))))

  var toolList: MutableList[ToolObject] = MutableList.empty

  def register(tool: ToolObject) = toolList += tool
  
}
