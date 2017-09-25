package controllers.tools

import play.mvc.Call

import ToolState._

case class Tool(name: String, state: ToolState, index: Call, decoration: String = null)

object ToolList {

  val tools = List(
    Tool("Boolesche Algebra", LIVE, controllers.bool.routes.BoolController.index()),
    Tool("Programmierung", LIVE, controllers.programming.routes.ProgController.index()),
    Tool("Sql", LIVE, controllers.sql.routes.SqlController.index()),
    Tool("Tabellenkalkulation", LIVE, controllers.spread.routes.SpreadController.index()),
    Tool("Web", LIVE, controllers.web.routes.WebController.index(), "<!DOCTYPE html>\n<html>...</html>"),
    Tool("Xml", LIVE, controllers.xml.routes.XmlController.index()),
    Tool("Zahlensysteme", LIVE, controllers.routes.NaryController.index()),

    // BETA-Tools
    Tool("Uml", BETA, controllers.uml.routes.UmlController.index()),
    Tool("Auswahlfragen", BETA, controllers.questions.routes.QuestionController.index()),

    // Alpha-Tools

    // Experimental Tools
    Tool("MindMap", EXPERIMENTAL, controllers.mindmap.routes.MindmapController.index()),
    Tool("Ebnf", EXPERIMENTAL, controllers.ebnf.routes.EBNFController.index()))

}
