package model.tools

object ToolList {

  val tools: List[Tool] = List(
    model.tools.ebnf.EbnfTool,
    model.tools.flask.FlaskTool,
    model.tools.programming.ProgrammingTool,
    model.tools.regex.RegexTool,
    //    model.tools.rose.RoseToolMain,
    model.tools.sql.SqlTool,
    model.tools.uml.UmlTool,
    model.tools.web.WebTool,
    model.tools.xml.XmlTool
  )

}
