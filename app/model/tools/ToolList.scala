package model.tools

object ToolList {

  val toolsWithoutParts: List[ToolWithoutParts] = List(
    model.tools.flask.FlaskTool,
    model.tools.regex.RegexTool,
    model.tools.sql.SqlTool
  )

  val toolsWithParts: List[ToolWithParts] = List(
    model.tools.programming.ProgrammingTool,
    model.tools.uml.UmlTool,
    model.tools.web.WebTool,
    model.tools.xml.XmlTool
  )

  lazy val tools: List[Tool] = (toolsWithoutParts ++ toolsWithParts).sortBy(_.name)

}
